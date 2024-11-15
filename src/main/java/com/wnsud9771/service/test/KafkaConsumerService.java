//package com.wnsud9771.service.test;
//
//import java.io.IOException;
//
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.core.KafkaAdmin;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import com.wnsud9771.dto.LogDTO;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class KafkaConsumerService {
//	
//	 
//	 @KafkaListener(topics = "START_LOG", groupId = "eitetlsss-1")
//	    public void consume(String message) throws IOException {
//	        System.out.printf("테스트로 CPG241023748토픽 컨슈밍: {}", message);
//	        log.info("테스트로 CPG241023748토픽 컨슈밍: {}",message);	        
//
//	    }
//
//	 
//}
