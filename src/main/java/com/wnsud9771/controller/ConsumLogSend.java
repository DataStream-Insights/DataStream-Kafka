package com.wnsud9771.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wnsud9771.dto.LogDTO;
import com.wnsud9771.service.KafkaConsumerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class ConsumLogSend {
	private final KafkaConsumerService kafkaConsumerService;
	
	@PostMapping("/send")
    public ResponseEntity<LogDTO> sendUserToSecondService(@RequestBody LogDTO logDto) {
        return ResponseEntity.ok(kafkaConsumerService.sendLogData(logDto));
    }
}
