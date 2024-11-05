package com.wnsud9771.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.wnsud9771.dto.LogDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecondHttpProducerService { //-------포매팅으로 받은 후 그걸 SECOND 토픽으로 보내는 ----
	private final KafkaTemplate<Integer, String> kafkaTemplate;
	
	 public LogDTO receiveLogData(LogDTO logDTO) {
		 
		 //들어온 로그 바로 kafka SECOND_HTTP 토픽으로 보내기
		 kafkaTemplate.send("SECOND_HTTP",logDTO.getLog_data());
		 return logDTO;
	 }
}
