//package com.wnsud9771.config.test;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//
//@EnableKafka
//@Configuration
//public class KafkaConsumerConfig {
//	@Value("${ec2port}")
//	private String serverport;
//	
//	//@Bean(name = "testConsumerFactory")
//    public ConsumerFactory<String, String> consumerFactory() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, serverport);
//       // props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 10000);
//        return new DefaultKafkaConsumerFactory<>(props);
//    }
//////commit
////    @Bean(name = "testKafkaListenerContainerFactory")
////    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
////        ConcurrentKafkaListenerContainerFactory<String, String> factory =
////            new ConcurrentKafkaListenerContainerFactory<>();
////        factory.setConsumerFactory(consumerFactory());
////        return factory;
////    }
//}
