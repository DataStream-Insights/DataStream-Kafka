package com.wnsud9771.dto.pipeline.add;

import java.util.List;

import lombok.Data;

@Data
public class AddFormatTopicDTO {
	private String formatId;
	
	private List<AddFilterTopicDTO> addFilterTopics;
}
