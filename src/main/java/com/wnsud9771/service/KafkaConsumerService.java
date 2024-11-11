//package com.wnsud9771.service;
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
//	 private final RestTemplate restTemplate;
//	 private static final String RECEIVE_LOG_URL = "http://localhost:8080/logs/receive";
//	 
//	 @KafkaListener(topics = "${topicId}", groupId = "consumer_group01")
//	    public void consume(String message) throws IOException {
//	        System.out.printf("Consumed Message : %s%n", message);
//	        log.info(message);	        
//	        LogDTO dto = new LogDTO();
//	        dto.setLog_data(message);
//	        sendLogData(dto);
//	    }
//	 
//	 public LogDTO sendLogData(LogDTO logDTO) {
//		 	log.info("send dto data {} : ", logDTO.getLog_data());
//	        return restTemplate.postForObject(RECEIVE_LOG_URL, logDTO, LogDTO.class);
//	    }
//	 
//}
