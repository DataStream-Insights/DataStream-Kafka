package com.wnsud9771.dto.pipeline.add;

import lombok.Data;

@Data
public class AddPipelineDTO {
	
	private String pipelineName;
	private String pipelineId;
	private Long distinctCode;
	
	private AddCampaignTopicDTO addcampaignTopic;
	
}
