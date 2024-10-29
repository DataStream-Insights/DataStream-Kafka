package com.wnsud9771.service;

import java.io.IOException;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaConsumerService {
	 @KafkaListener(topics = "START_HTTP", groupId = "consumer_group01")
	    public void consume(String message) throws IOException {
	        System.out.printf("Consumed Message : %s%n", message);
	        log.info(message);
	    }
}
