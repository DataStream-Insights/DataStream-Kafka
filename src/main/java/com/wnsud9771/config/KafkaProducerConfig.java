package com.wnsud9771.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@EnableKafka
@Configuration	
public class KafkaProducerConfig {

	
	@Bean
	public ProducerFactory<Integer, String> producerFactory() {
	    return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	@Bean
	public Map<String, Object> producerConfigs() {
	    Map<String, Object> props = new HashMap<>();
	    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
	    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
	    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	    // See https://kafka.apache.org/documentation/#producerconfigs for more properties
	    
	 // 비동기 전송을 위한 최적화 설정
        props.put(ProducerConfig.ACKS_CONFIG, "1");  // 리더만 응답 기다림
        props.put(ProducerConfig.RETRIES_CONFIG, 3); // 재시도 횟수
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 100); // 재시도 간격
        
        // 성능 최적화 설정
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);  // 배치 크기
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);       // 배치 대기 시간
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432); // 버퍼 메모리
        
        // 비동기 처리를 위한 설정
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
	    return props;
	}

	@Bean
	public KafkaTemplate<Integer, String> kafkaTemplate() {
	    return new KafkaTemplate<Integer, String>(producerFactory());
	}
}
