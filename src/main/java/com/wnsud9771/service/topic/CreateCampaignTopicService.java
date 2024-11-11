package com.wnsud9771.service.topic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.stereotype.Service;

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

	private static final String SOURCE_TOPIC = "tpic";
	private static final String CAMPAIGN_TOPIC_PREFIX = "campaign-";

	// -----------------------------------( 함수들 호출 로직 )-----------------------------------------------------
	public void createTopicAndSendLog(String campaignId) {
		String newTopicName = CAMPAIGN_TOPIC_PREFIX + campaignId;
		// 먼저 토픽 생성
		createTopicIfNotExists(newTopicName);

		// start_log 에서 컨슈밍으로 받은 로그를(새로생성이니 earliest로 토픽안에 전부 읽어서 적재) 그다음 바로 새로만든 토픽으로
		// 프로듀싱해서 적재
		setupConsumer(campaignId);
		
		log.info("새로운 토픽 생성 및 해당 토픽 컨슈밍 + 프로듀싱 성공");
		
	}

	// --------------------------------------------------------------------------------------------------
	
	
	

	// -------------------------------------( 토픽 조회후 없으면 생성 )------------------------------------------
	public void createTopicIfNotExists(String newtopicName) {
		// Kafka Admin Client 설정
		Properties props = new Properties();
		props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");

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
			log.error("Error creating/checking topic {}: ", newtopicName, e);
			throw new RuntimeException("Topic creation failed", e);
		}
	}
	// ----------------------------------------------------------------------------------------------------------
	
	
	
	

	// ---------------------------------(컨슈머세팅 각 토픽마다 새컨슈머로 )-----------------------------------------------
	private void setupConsumer(String campaignId) {
		String targetTopic = CAMPAIGN_TOPIC_PREFIX + campaignId;
		String groupId = "campaign-consumer-" + campaignId;

		ConsumerFactory<String, String> consumerFactory = createConsumerFactory(groupId);
		ContainerProperties containerProps = new ContainerProperties(SOURCE_TOPIC);
		
		containerProps.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

		containerProps.setMessageListener((AcknowledgingMessageListener<String, String>) (record, acknowledgment) -> {
			try {
				log.info("START_LOG에서 컨슈밍한 오프셋 : {},// 로그 : {} ", record.offset(),record.value() );

				kafkaTemplate.send(targetTopic, record.value()); //해당 토픽에 적재
				log.info("{} 토픽으로 {} 전송 성공",targetTopic,record.value());
				acknowledgment.acknowledge(); // 성공시 커밋
			} catch (Exception e) {
				log.error(" {} 토픽에서 오류 생김 ", targetTopic, e);
			}
		});

	}
	// ----------------------------------------------------------------------------------------------------------
	
	
	

	// -----------------------------------------( 컨슈머 팩토리 설정 )---------------------------------------------------
	private ConsumerFactory<String, String> createConsumerFactory(String groupId) {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
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
	 @PreDestroy
	    public void cleanup() {
	        consumers.forEach((topic, container) -> {
	            container.stop();
	            log.info("Stopped consumer for topic: {}", topic);
	        });
	        consumers.clear();
	    }
	 
	 //-------------------------------------------------------------------------------------------------------------
	 
	 
}
