package com.wnsud9771.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wnsud9771.dto.pipeline.add.AddPipelineDTO;
import com.wnsud9771.service.PipelineService;
import com.wnsud9771.service.PipelineStopService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ids")
@RequiredArgsConstructor
@Slf4j
public class ReceiveIdsController {
	private final PipelineService pipelineService;
	private final PipelineStopService pipelineStopService; 
	
	@PostMapping("/start")
	public ResponseEntity<AddPipelineDTO> receiveUserFromFirstService(@RequestBody AddPipelineDTO dto) {
		log.info("받은 파이프라인 id : {}", dto.getPipelineId());
		
		pipelineService.receiveidsandprocessing(dto);
		
		
		return ResponseEntity.ok(dto);
	}
	
	@PostMapping("/stop")
	public ResponseEntity<AddPipelineDTO> receivestopPipelineService(@RequestBody AddPipelineDTO dto) {
		log.info("받은 파이프라인 id : {}", dto.getPipelineId());
		
		pipelineStopService.receiveidsandstopprocessing(dto);
		
		
		return ResponseEntity.ok(dto);
	}
}
