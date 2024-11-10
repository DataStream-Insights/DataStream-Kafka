package com.wnsud9771.service;

public class DynamicKafkaConsumerService {
	private final AdminClient adminClient;
    private final KafkaListenerEndpointRegistry registry;
    private final ConsumerFactory<String, String> consumerFactory;
    private final List<String> monitoredTopics = new ArrayList<>();
    private final String sourceTopicPattern = "source-topic.*"; // 모니터링할 토픽 패턴
    
    @Autowired
    public DynamicKafkaConsumerService(
            AdminClient adminClient,
            KafkaListenerEndpointRegistry registry,
            ConsumerFactory<String, String> consumerFactory) {
        this.adminClient = adminClient;
        this.registry = registry;
        this.consumerFactory = consumerFactory;
        
        // 주기적으로 새로운 토픽 확인
        scheduleTopicCheck();
    }
    
    private void scheduleTopicCheck() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::checkForNewTopics, 0, 1, TimeUnit.MINUTES);
    }
    
    private void checkForNewTopics() {
        try {
            ListTopicsResult topics = adminClient.listTopics();
            Set<String> currentTopics = topics.names().get();
            
            for (String topic : currentTopics) {
                if (topic.matches(sourceTopicPattern) && !monitoredTopics.contains(topic)) {
                    addNewConsumer(topic);
                    monitoredTopics.add(topic);
                }
            }
        } catch (Exception e) {
            log.error("토픽 확인 중 에러 발생", e);
        }
    }
    
    private void addNewConsumer(String topicName) {
        // 동적으로 새로운 컨슈머 생성
        ConcurrentMessageListenerContainer<String, String> container = 
            new ConcurrentMessageListenerContainer<>(consumerFactory, 
                new ContainerProperties(topicName));
                
        container.setupMessageListener((MessageListener<String, String>) record -> {
            processMessage(record);
        });
        
        // 컨슈머 시작
        container.start();
        log.info("새로운 토픽 {} 에 대한 컨슈머가 시작되었습니다.", topicName);
    }
    
    private void processMessage(ConsumerRecord<String, String> record) {
        try {
            log.info("토픽: {}, 파티션: {}, 오프셋: {}, 메시지: {}", 
                record.topic(), record.partition(), record.offset(), record.value());
            // 여기에 메시지 처리 로직 구현
        } catch (Exception e) {
            log.error("메시지 처리 중 에러 발생", e);
        }
    }
    
    // 애플리케이션 종료 시 리소스 정리
    @PreDestroy
    public void cleanup() {
        adminClient.close();
    }
}
