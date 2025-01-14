package com.wnsud9771.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class StringKafkaProducerConfig {
	@Value("${ec2port}")
	private String serverport;
	 @Bean
	    public ProducerFactory<String, String> stringProducerFactory() {
	        Map<String, Object> props = new HashMap<>();
	        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverport);
	        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	        
	        // 비동기 전송을 위한 최적화 설정
	        props.put(ProducerConfig.ACKS_CONFIG, "1");
	        props.put(ProducerConfig.RETRIES_CONFIG, 3);
	        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 100);
	        
	        // 성능 최적화 설정
	        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
	        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
	        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
	        
	        // 비동기 처리를 위한 설정
	        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
	        
	        return new DefaultKafkaProducerFactory<>(props);
	    }

	    @Bean(name = "stringKafkaTemplate")
	    public KafkaTemplate<String, String> stringKafkaTemplate(ProducerFactory<String, String> stringProducerFactory) {
	    	 return new KafkaTemplate<String, String>(stringProducerFactory);
	    }
}	
