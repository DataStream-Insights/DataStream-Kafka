//package com.wnsud9771.controller;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.wnsud9771.dto.LogDTO;
//import com.wnsud9771.service.SecondHttpProducerService;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//@RestController
//@RequestMapping("/logs")
//@RequiredArgsConstructor
//@Slf4j
//public class FormatingLogController {
//	private final SecondHttpProducerService secondHttpProducerService; 
//	
//	@PostMapping("/formating")
//    public ResponseEntity<LogDTO> receiveUserFromFirstService(@RequestBody LogDTO logDTO) {
//		log.info("log data {}:", logDTO.getLog_data());
//        return ResponseEntity.ok(secondHttpProducerService.receiveLogData(logDTO));
//    } 
//	
//}
