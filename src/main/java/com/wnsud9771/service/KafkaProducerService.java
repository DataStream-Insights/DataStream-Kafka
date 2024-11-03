package com.wnsud9771.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
	private final KafkaTemplate<Integer, String> kafkaTemplate;
	String data =  "{\"title\":\"사용자 A의 전자제품 조회 로그\",\"contents\":{\"timestamp\":\"2024-10-31T14:23:45+09:00\","
			+ "\"visitor_id\":\"2cff4a12e87f499b\",\"url\":\"https://example.com/products/category/electronics\","
			+ "\"event_action\":\"View\",\"user_id\":\"user_123456\"}}";
	
	@PostConstruct //임시로 스프링 시작할때 토픽으로 데이터쏘기
    public void init() {
        sendToKafka();
    }
	
	public void sendToKafka() {
	    kafkaTemplate.send("START_HTTP",data);
	    System.out.println(" ");
	    System.out.println("메시지 전송 완료!");
	    System.out.println(" ");
	}
	
	
}
