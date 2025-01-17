package com.wnsud9771.service.topic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
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

import com.wnsud9771.service.mybatis.UpdateConsumerService;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class CreateCampaignTopicService {

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final AdminClient adminClient;
	private final Map<String, ConcurrentMessageListenerContainer<String, String>> consumers = new ConcurrentHashMap<>();
	private final UpdateConsumerService updateConsumerService;
	
	private static final String SOURCE_TOPIC = "START_LOG";
	private static final String CAMPAIGN_TOPIC_PREFIX = "campaign-";
	
	@Value("${ec2port}")
	private String serverport;

	// -----------------------------------( 함수들 호출 로직
	// )-----------------------------------------------------
	public void createTopicAndSendLog(String pipelineId, String campaignId) {
		String newTopicName = pipelineId + campaignId;
		// 먼저 토픽 생성
		createTopicIfNotExists(newTopicName);

		// start_log 에서 컨슈밍으로 받은 로그를(새로생성이니 earliest로 토픽안에 전부 읽어서 적재) 그다음 바로 새로만든 토픽으로
		// 프로듀싱해서 적재
		setupConsumer(pipelineId, campaignId);

		log.info("새로운 토픽 생성 및 해당 토픽 컨슈밍 + 프로듀싱 성공");

	}

	// --------------------------------------------------------------------------------------------------

	// -------------------------------------( 토픽 조회후 없으면 생성 // )------------------------------------------
	public void createTopicIfNotExists(String newtopicName) {
		// Kafka Admin Client 설정
		Properties props = new Properties();
		props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, serverport);

		// Admin Client 사용 (try-with-resources로 자동 리소스 해제)
		try (AdminClient adminClient = AdminClient.create(props)) {
			// 존재하는 모든 토픽 조회
			ListTopicsResult listTopics = adminClient.listTopics();
			Set<String> existingTopics = listTopics.names().get();

			// 토픽이 존재하지 않는 경우에만 새로 생성
			if (!existingTopics.contains(newtopicName)) {
				NewTopic newTopic = new NewTopic(newtopicName, 1, (short) 1);
				adminClient.createTopics(Collections.singleton(newTopic)).all().get();
				log.info("Created new topic: {}", newtopicName);
			}
		} catch (Exception e) {
			//log.error(" {} : 캠페인 토픽이 생성 안됨 ", newtopicName, e);
			log.info("{}:토픽 이미 생성되어있거나 오류로인한 미생성", newtopicName);
			// 생성 안된 캠페인
			//String failcampaignId = newtopicName.substring(CAMPAIGN_TOPIC_PREFIX.length());

			throw new RuntimeException("Topic creation failed", e);
		}
	}
	// ----------------------------------------------------------------------------------------------------------

	// ---------------------------------(컨슈머세팅 각 토픽마다 새컨슈머로// )-----------------------------------------------
	private void setupConsumer(String pipelineId, String campaignId) {
		String targetTopic = pipelineId + campaignId;
		String groupId = pipelineId + campaignId;

		ConsumerFactory<String, String> consumerFactory = createConsumerFactory(groupId);
		ContainerProperties containerProps = new ContainerProperties(SOURCE_TOPIC);


		containerProps.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);


		containerProps.setMessageListener((AcknowledgingMessageListener<String, String>) (record, acknowledgment) -> {
			try {
				log.info("START_LOG에서 컨슈밍한 오프셋 : {}, 로그 : {} ", record.offset(), record.value());


				// 비동기 전송 처리
				CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(targetTopic, record.value());

				future.whenComplete((result, ex) -> {
					if (ex == null) {
						// 전송 성공시에만 커밋
						acknowledgment.acknowledge();
						log.info("{} 토픽으로 {} 전송 성공", targetTopic, record.value());
						
						// 컨슈머 생성됬으니 해당 캠페인 id db 컨슈머 true로 변경
						
						
						
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
		        updateConsumerService.updateCampaignConsumer(pipelineId, campaignId, true, targetTopic);
		 }
		consumers.put(targetTopic, container);

	}
	// ----------------------------------------------------------------------------------------------------------

	// -----------------------------------------( 컨슈머 팩토리 설정// )---------------------------------------------------
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

	// -------------------------------------( 프로젝트 종료시 컨슈머 정리 // )----------------------------------------------------
	//@PreDestroy
	public void cleanup(String pipelineId, String campaignId) {
		String campaign_topics = pipelineId +campaignId;
		ConcurrentMessageListenerContainer<String, String> container = consumers.remove(campaign_topics);
		if (container != null) {
		    container.stop();
		    updateConsumerService.stopCampaignConsumer(pipelineId, campaignId, false);
		}else {
			log.info("해당 토픽 에대한 컨슈머가 실행되어있지 않습니다. consumer :{}", campaign_topics);
		}
		
//		consumers.forEach((topic, container) -> {
//			container.stop();
//			log.info("Stopped consumer for topic: {}", topic);
//		});
//		consumers.clear();
	}

	// -------------------------------------------------------------------------------------------------------------

}
