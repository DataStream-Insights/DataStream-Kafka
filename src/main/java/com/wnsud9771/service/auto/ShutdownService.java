package com.wnsud9771.service.auto;

import org.springframework.stereotype.Service;

import com.wnsud9771.mapper.PipelineStatusMapper;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShutdownService {
	private final PipelineStatusMapper pipelineStatusMapper;

	@PreDestroy
	public void cleanup() {

		try {
		log.info("Starting pipeline status cleanup...");
		pipelineStatusMapper.resetAllPipelineStatus();		
		pipelineStatusMapper.resetCampaignTopicStatus();
		pipelineStatusMapper.resetFormatTopicStatus();
		pipelineStatusMapper.resetFilterTopicStatus();
		log.info("Successfully reset pipeline status on shutdown");
	} catch (Exception e) {
		log.error("Failed to reset pipeline status on shutdown", e);
	}
}
}


