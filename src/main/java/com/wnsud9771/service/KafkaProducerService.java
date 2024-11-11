package com.wnsud9771.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
	private final KafkaTemplate<Integer, String> kafkaTemplate;
	String data = "{\"title\":\"사용자 A의 전자제품 조회 로그\",\"contents\":{\"timestamp\":\"2024-10-31T14:23:45+09:00\","
			+ "\"visitor_id\":\"2cff4a12e87f499b\",\"url\":\"https://example.com/products/category/electronics\","
			+ "\"event_action\":\"View\",\"user_id\":\"user_123456\"}}";
	String data2 = "{\"title\":\"사용자 B의 의류 조회 로그\",\"contents\":{\"timestamp\":\"2024-10-31T14:25:12+09:00\","
			+ "\"visitor_id\":\"3dff5b23f98g500c\",\"url\":\"https://example.com/products/category/clothing\","
			+ "\"event_action\":\"View\",\"user_id\":\"user_123457\"}}";
	String data3 = "{\"title\":\"사용자 C의 장바구니 추가 로그\",\"contents\":{\"timestamp\":\"2024-10-31T14:28:33+09:00\","
			+ "\"visitor_id\":\"4eff6c34g09h501d\",\"url\":\"https://example.com/products/category/electronics\","
			+ "\"event_action\":\"Add_to_Cart\",\"user_id\":\"user_123458\"}}";
	String data4 = "{\"title\": \"회사 조직도\",\"company\": {\"name\": \"테크솔루션\",\"departments\": {\"name\": \"개발본부\","
			+ "\"teams\": {\"name\": \"모바일팀\",\"headcount\": 15,\"projects\": [\"안드로이드\", \"iOS\"]},\"budget\": 500000000,\"location\": "
			+ "{\"building\": \"A동\",\"floor\": 5,\"seats\": {\"total\": 50,\"occupied\": 35}}},\"status\": \"active\",\"branches\": {\"count\": 3,\"locations\": "
			+ "[\"서울\", \"부산\", \"대전\"],\"management\": {\"type\": \"분산형\",\"controls\": {\"level\": \"중앙집중식\",\"authority\": \"본사직할\"}}}}}";

	@PostConstruct // 임시로 스프링 시작할때 토픽으로 데이터쏘기
	public void init() {
		sendToKafka();
	}

	public void sendToKafka() {
		// -----------------쇼핑몰에서 보냈다고 치고 보내는 로그데이터--------------------------------
		kafkaTemplate.send("tpic", data);
		kafkaTemplate.send("tpic", data2);
		kafkaTemplate.send("tpic", data3);
		kafkaTemplate.send("tpic", data4);

		System.out.println(" ");
		System.out.println("메시지 전송 완료!");
		System.out.println(" ");
	}

}
