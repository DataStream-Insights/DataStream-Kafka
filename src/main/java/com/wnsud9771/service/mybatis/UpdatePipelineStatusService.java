package com.wnsud9771.service.mybatis;

import org.springframework.stereotype.Service;

import com.wnsud9771.mapper.PipelineStatusMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdatePipelineStatusService {
	private final PipelineStatusMapper pipelineStatusMapper;
	
	public void changePipelineStatus(String pipelineId) {
		if(pipelineStatusMapper.areAllFilterConsumersTrue(pipelineId) == true) {
			 pipelineStatusMapper.updatePipelineStatus(pipelineId, true);
		}else {
			 pipelineStatusMapper.updatePipelineStatus(pipelineId, false);
		}
		
	}

}
