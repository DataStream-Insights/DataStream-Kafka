package com.wnsud9771.service.topic;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.wnsud9771.dto.format.CampaignIdFormatIdDTO;
import com.wnsud9771.mapper.FormatMapper;
import com.wnsud9771.service.format.parsing.FormatingService;
import com.wnsud9771.service.mybatis.MybatisService;
import com.wnsud9771.service.mybatis.UpdateConsumerService;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class CreateFormatTopicService {

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final AdminClient adminClient;
	private final Map<String, ConcurrentMessageListenerContainer<String, String>> consumers = new ConcurrentHashMap<>();
	private final FormatingService formatingService;
	private final FormatMapper formatMapper;
	private final MybatisService mybatisService;
	private final UpdateConsumerService updateConsumerService;
	
	@Value("${ec2port}")
	private String serverport;

	//private static final String SOURCE_TOPIC = "tpic";
	//private static final String CAMPAIGN_TOPIC_PREFIX = "campaign-";

	// -----------------------------------( 함수들 호출 로직 )-----------------------------------------------------
	public boolean createTopicAndSendLog(String pipelineId, String campaignId,String formatId) {
		String newTopicName = pipelineId + campaignId + formatId;
		try {
			// 먼저 토픽 생성
			if(!createTopicIfNotExists(newTopicName, campaignId, formatId)) {
				log.info("토픽 생성 실패: {}", newTopicName);
				return false;
			}
			
			log.info("{}: 새로운 포맷 토픽 생성 성공",newTopicName);
			if(setupConsumer(pipelineId, campaignId, formatId)) {
				log.info("새로생긴 포맷 토픽 포맷팅 작업, 포맷 토픽에 프로듀싱");
				return true;
			}
			
			return true;
		}catch(Exception e) {
			log.info("{}: 토픽, 에러메시지: {}", newTopicName, e.getMessage());
			return false;
		}
	}

	// --------------------------------------------------------------------------------------------------
	
	
	

	// -------------------------------------( 토픽 조회후 없으면 생성 )------------------------------------------
	public boolean createTopicIfNotExists(String newtopicName,String campaignId,String formatId) {
//		// Kafka Admin Client 설정
//		Properties props = new Properties();
//		props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");

		// Admin Client 사용 (try-with-resources로 자동 리소스 해제)
		try {
			
			// 존재하는 모든 토픽 조회
			ListTopicsResult listTopics = adminClient.listTopics();
			Set<String> existingTopics = listTopics.names().get();

			// 토픽이 존재하지 않는 경우에만 새로 생성
			if (!existingTopics.contains(newtopicName)) {
				NewTopic newTopic = new NewTopic(newtopicName, 1, (short) 1);
				adminClient.createTopics(Collections.singleton(newTopic)).all().get();
				log.info("Created new topic: {}", newtopicName);
			}else {
//				CampaignIdFormatIdDTO dto = new CampaignIdFormatIdDTO();
//				dto.setCampaignId(campaignId);
//				dto.setFormatId(formatId);
//				mybatisService.failformatTopic(dto);
				log.info("{}:토픽 이미 생성되어있거나 오류로인한 미생성", newtopicName);
			}
			
			return true;
		} catch (Exception e) {
			log.error("Error creating/checking topic {}: ", newtopicName, e);
			//throw new RuntimeException("Topic creation failed", e);
			return false;
		}
	}
	// ----------------------------------------------------------------------------------------------------------
	
	
	
	

	// ---------------------------------(컨슈머세팅 각 토픽마다 새컨슈머로 )-----------------------------------------------
	private boolean setupConsumer(String pipelineId, String campaignId, String formatId) {
		String consumeTopic = pipelineId + campaignId;
		String targetTopic =pipelineId + campaignId + formatId;
		String groupId = pipelineId + campaignId + formatId;
		List<String> paths = formatMapper.findPathsByFormatId(formatId);
		
		log.info("setupConsumer() paths {}",paths);

		ConsumerFactory<String, String> consumerFactory = createConsumerFactory(groupId);
		ContainerProperties containerProps = new ContainerProperties(consumeTopic);
		containerProps.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
		//각 토픽들의 컨테이너 만듬(컨슈머)
	

		containerProps.setMessageListener((AcknowledgingMessageListener<String, String>) (record, acknowledgment) -> {
	        try {
	            
	            // 포매팅 처리
	            String parsinglog = formatingService.parsingLog(record.value(), paths).getParsedLog();
	            log.info("컨슈밍으로 받은 로그 ㅣ::::{}", record.value());
	            
	            // 비동기 전송 처리
	            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(targetTopic, parsinglog);
	            
	            future.whenComplete((result, ex) -> {
	                if (ex == null) {
	                    // 전송 성공시에만 커밋
	                    acknowledgment.acknowledge();
	                    log.info("{} 토픽으로 {} 전송 성공", targetTopic, parsinglog);
	                    
	                    
	                } else {
	                    log.error("Failed to send message to topic: {}", targetTopic, ex);
	                    // 실패시 커밋하지 않음 - 메시지 재전송됨
	                }
	            });
	            
	        } catch (Exception e) {
	            log.error("{} 토픽에서 오류 생김 ", targetTopic, e);
	            // 예외 발생시 커밋하지 않음 - 메시지 재전송됨
	        }
	    });

		ConcurrentMessageListenerContainer<String, String> container = new ConcurrentMessageListenerContainer<>(consumerFactory, containerProps);
	    container.start();
	    if (container.isRunning()) {
	        log.info("Container for topic {} is now running", targetTopic);
	        updateConsumerService.updateFormatConsumer(pipelineId, campaignId, formatId, true, targetTopic);
	    }
	    consumers.put(targetTopic, container);
	    log.info("Successfully set up consumer for {} -> {}", consumeTopic, targetTopic);
	    
	    return true;
	}
	// ----------------------------------------------------------------------------------------------------------
	
	
	

	// -----------------------------------------( 컨슈머 팩토리 설정 )---------------------------------------------------
	private ConsumerFactory<String, String> createConsumerFactory(String groupId) {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, serverport);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		return new DefaultKafkaConsumerFactory<>(props);
	}
	// ---------------------------------------------------------------------------------------------------------------
	
	
	
	
	
	//-------------------------------------( 프로젝트 종료시 컨슈머 정리 )----------------------------------------------------
	// @PreDestroy
	    public void cleanup(String pipelineId, String campaignId, String formatId) {
	    	String format_topics = pipelineId + campaignId + formatId;
	    	ConcurrentMessageListenerContainer<String, String> container = consumers.remove(format_topics);
			if (container != null) {
			    container.stop();
			    updateConsumerService.stopFormatConsumer(pipelineId, campaignId, formatId, false);
			}else {
				log.info("해당 토픽 에대한 컨슈머가 실행되어있지 않습니다. consumer :{}",format_topics);
			}
			
//			consumers.forEach((topic, container) -> {
//				container.stop();
//				log.info("Stopped consumer for topic: {}", topic);
//			});
//			consumers.clear();
	    }
	 
	 //-------------------------------------------------------------------------------------------------------------
	 
	 
}
