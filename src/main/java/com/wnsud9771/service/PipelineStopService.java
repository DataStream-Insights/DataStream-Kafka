package com.wnsud9771.service;

import org.springframework.stereotype.Service;

import com.wnsud9771.dto.campaign.CampaignIdDTO;
import com.wnsud9771.dto.pipeline.add.AddFilterTopicDTO;
import com.wnsud9771.dto.pipeline.add.AddFormatTopicDTO;
import com.wnsud9771.dto.pipeline.add.AddPipelineDTO;
import com.wnsud9771.service.mybatis.UpdatePipelineStatusService;
import com.wnsud9771.service.topic.CreateCampaignTopicService;
import com.wnsud9771.service.topic.CreateFilterTopicService;
import com.wnsud9771.service.topic.CreateFormatTopicService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class PipelineStopService {
	private final CreateCampaignTopicService createCampaignTopicService;
	private final CreateFormatTopicService createFormatTopicService;
	private final CreateFilterTopicService createFilterTopicService;
	private final UpdatePipelineStatusService updatePipelineStatusService;

	public void receiveidsandstopprocessing(AddPipelineDTO receivedto) {
		// CampaignTopic 토픽, 컨슈머 생성.
		if (receivedto.getAddcampaignTopic() != null) {
			CampaignIdDTO campaignIddto = new CampaignIdDTO();
			campaignIddto.setCampaingId(receivedto.getAddcampaignTopic().getCampaignId());
			
			
			
			createCampaignTopicService.cleanup(receivedto.getPipelineId(), campaignIddto.getCampaingId());

			// FormatTopic 토픽,컨슈머 생성
			if (receivedto.getAddcampaignTopic().getAddFormatTopics() != null) {
				for (AddFormatTopicDTO addformatTopicdto : receivedto.getAddcampaignTopic().getAddFormatTopics()) {

					
					createFormatTopicService.cleanup(receivedto.getPipelineId(), campaignIddto.getCampaingId(), addformatTopicdto.getFormatId());

					// 필터토픽 ,컨슈머 생성
					if (addformatTopicdto.getAddFilterTopics() != null) {
						for (AddFilterTopicDTO filterTopicdto : addformatTopicdto.getAddFilterTopics()) {
							
							createFilterTopicService.cleanup(receivedto.getPipelineId(), addformatTopicdto.getFormatId(), filterTopicdto.getFilterId());	
							
						}
					}
				}
			}
		}
		
		//전부 중지후 조회해서 전부 consumer false이면 중지.
		updatePipelineStatusService.changePipelineStatus(receivedto.getPipelineId());
		
	}
}
