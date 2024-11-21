package com.wnsud9771.dto.pipeline.add;

import java.util.List;

import lombok.Data;

@Data
public class AddCampaignTopicDTO {
	
	private String campaignId;
	
	private List<AddFormatTopicDTO> addFormatTopics;
}
