package com.wnsud9771.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wnsud9771.dto.pipeline.add.AddPipelineDTO;
import com.wnsud9771.service.PipelineService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ids")
@RequiredArgsConstructor
@Slf4j
public class ReceiveIdsController {
	private final PipelineService pipelineService;
	
	@PostMapping("/")
	public ResponseEntity<AddPipelineDTO> receiveUserFromFirstService(@RequestBody AddPipelineDTO dto) {
		log.info("받은 파이프라인 id : {}", dto.getPipelineId());
		
		pipelineService.receiveidsandprocessing(dto);
		
		
		return ResponseEntity.ok(dto);
	}
}
