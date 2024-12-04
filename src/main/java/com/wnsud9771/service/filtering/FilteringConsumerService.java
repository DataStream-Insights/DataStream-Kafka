package com.wnsud9771.service.filtering;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.stereotype.Service;

import com.wnsud9771.mapper.FormatMapper;
import com.wnsud9771.service.be.BesendService;
import com.wnsud9771.service.format.parsing.FormatingService;
import com.wnsud9771.service.mybatis.MybatisService;
import com.wnsud9771.service.mybatis.UpdateConsumerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilteringConsumerService {
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final AdminClient adminClient;
	private final Map<String, ConcurrentMessageListenerContainer<String, String>> consumers = new ConcurrentHashMap<>();
	private final FormatingService formatingService;
	private final FormatMapper formatMapper;
	private final MybatisService mybatisService;
	private final UpdateConsumerService updateConsumerService;
	private final FilteringSubmitService  filteringSubmitService;
	private final BesendService besendService;
	
	@Value("${ec2port}")
	private String serverport;

	// ---------------------------------(컨슈머세팅 각 토픽마다 새컨슈머로 )-----------------------------------------------
	public void setupConsumer(String pipelineId, String filterTopic,Long distinctCode) {
		String consumeTopic = filterTopic;
		String groupId = filterTopic + pipelineId;
		

		ConsumerFactory<String, String> consumerFactory = createConsumerFactory(groupId);
		ContainerProperties containerProps = new ContainerProperties(consumeTopic);
		containerProps.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
		//각 토픽들의 컨테이너 만듬(컨슈머)
	

		containerProps.setMessageListener((AcknowledgingMessageListener<String, String>) (record, acknowledgment) -> {
	        try {
	            
	            log.info("컨슈밍으로 받은 로그 ㅣ::::{}", record.value());
	            
	            
	            filteringSubmitService.filtersubmitsuccessorfail(pipelineId, record.value(),distinctCode);
	            besendService.sendbeanddb(pipelineId);
	           acknowledgment.acknowledge();
	     
	            
	          
	            
	        } catch (Exception e) {
	            log.error("{} 토픽에서 오류 생김 ", filterTopic, e);
	            // 예외 발생시 커밋하지 않음 - 메시지 재전송됨
	        }
	    });

		ConcurrentMessageListenerContainer<String, String> container = new ConcurrentMessageListenerContainer<>(consumerFactory, containerProps);
	    container.start();
	    consumers.put(consumeTopic, container);
	    log.info("Successfully set up consumer for {} ", consumeTopic);
	    
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
//	// @PreDestroy
//	    public void cleanup() {
//	    	String format_topics = pipelineId + campaignId + formatId;
//	    	ConcurrentMessageListenerContainer<String, String> container = consumers.remove(format_topics);
//			if (container != null) {
//			    container.stop();
//			    updateConsumerService.stopFormatConsumer(pipelineId, campaignId, formatId, false);
//			}else {
//				log.info("해당 토픽 에대한 컨슈머가 실행되어있지 않습니다. consumer :{}",format_topics);
//			}
//			
////			consumers.forEach((topic, container) -> {
////				container.stop();
////				log.info("Stopped consumer for topic: {}", topic);
////			});
////			consumers.clear();
//	    }
//	 
	 //-------------------------------------------------------------------------------------------------------------
	 
}
