package com.wnsud9771.service.auto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class AllConsumeService {// 프로젝트가 시작될때 기존 토픽들 모두 컨슈밍하기 latest로 안읽은 데이터부터 다 읽기 + 프로듀싱으로 적재
	private final KafkaTemplate<String, String> kafkaTemplate;
    private final AdminClient adminClient;
    private final Map<String, ConcurrentMessageListenerContainer<String, String>> consumers = new ConcurrentHashMap<>();
    
    private static final String SOURCE_TOPIC = "START_LOG";
    private static final String CAMPAIGN_TOPIC_PREFIX = "campaign-";
    
    
    //---------------------------( 프로젝트 시작될때 기존 토픽 조회 후 컨슈머들 실행 )--------------------------------------------------
    @PostConstruct
    public void init() {
        try {
            log.info("Starting to search for existing campaign topics...");
            Set<String> topics = adminClient.listTopics().names().get();
            
            List<String> campaignTopics = topics.stream()
                .filter(topic -> topic.startsWith(CAMPAIGN_TOPIC_PREFIX))
                .collect(Collectors.toList());
                
            log.info("Found {} existing campaign topics: {}", campaignTopics.size(), campaignTopics);
            
            campaignTopics.forEach(topic -> {
                String campaignId = topic.substring(CAMPAIGN_TOPIC_PREFIX.length());
                log.info("Setting up consumer for START_LOG → {}", topic);
                setupConsumer(campaignId);
            });
            
        } catch (Exception e) {
            log.error("Failed to initialize existing campaign consumers", e);
        }
    }
    
    //----------------------------------------------------------------------------------------------------------------------
    
    
    //-------------------------------------------------------( 컨슈머 설정 )--------------------------------------------------------------
    private void setupConsumer(String campaignId) {
        String targetTopic = CAMPAIGN_TOPIC_PREFIX + campaignId;
        String groupId = "campaign-consumer-" + campaignId;
        
        ConsumerFactory<String, String> consumerFactory = createConsumerFactory(groupId);
        ContainerProperties containerProps = new ContainerProperties(SOURCE_TOPIC);
        
        // 수동 커밋 설정
        containerProps.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        
        containerProps.setMessageListener((AcknowledgingMessageListener<String, String>) (record, acknowledgment) -> {
            try {
                // 1. campaign 토픽으로 전송
                kafkaTemplate.send(targetTopic,record.value());
                
                acknowledgment.acknowledge(); //전송 성공시 커밋
                log.debug("메시지 전송 오프셋 : {} , 토픽 : {} ", record.offset(), targetTopic);
                
            } catch (Exception e) {
                log.error("해당 토픽 오류 : {} ", targetTopic, e);
            }
        });
        
        //각 토픽들의 컨테이너 만듬(컨슈머)
        ConcurrentMessageListenerContainer<String, String> container = new ConcurrentMessageListenerContainer<>(consumerFactory, containerProps);
            
        consumers.put(targetTopic, container);
        container.start();
        
        log.info("   {} 토픽의 컨슈머 실행(latest) ", targetTopic);
    }
    
    //-------------------------( 컨슈머 팩토리 설정 ) ----------------------------------------------
    
    
    private ConsumerFactory<String, String> createConsumerFactory(String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }
    
    @PreDestroy
    public void cleanup() {
        consumers.forEach((topic, container) -> {
            container.stop();
            log.info("Stopped consumer for topic: {}", topic);
        });
        consumers.clear();
    }
}
