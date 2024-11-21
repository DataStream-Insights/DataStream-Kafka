package com.wnsud9771.service;

import org.springframework.context.ApplicationEventPublisher;
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
public class PipelineService {
	private final CreateCampaignTopicService createCampaignTopicService;
	private final CreateFormatTopicService createFormatTopicService;
	private final CreateFilterTopicService createFilterTopicService;
	private final UpdatePipelineStatusService updatePipelineStatusService;
	private final ApplicationEventPublisher eventPublisher;
	
	public void receiveidsandprocessing(AddPipelineDTO receivedto) {
		//receivedto.getPipelineId();
		
		// CampaignTopic 토픽, 컨슈머 생성.
				if (receivedto.getAddcampaignTopic() != null) {
					CampaignIdDTO campaignIddto = new CampaignIdDTO();
					campaignIddto.setCampaingId(receivedto.getAddcampaignTopic().getCampaignId());
					
					createCampaignTopicService.createTopicAndSendLog(receivedto.getPipelineId(),campaignIddto.getCampaingId());
					
					// FormatTopic 토픽,컨슈머 생성
					if (receivedto.getAddcampaignTopic().getAddFormatTopics() != null) {
						for (AddFormatTopicDTO addformatTopicdto : receivedto.getAddcampaignTopic().getAddFormatTopics()) {
//							CampaignIdFormatIdDTO campaignIdFormatIdDTO = new CampaignIdFormatIdDTO();
//							campaignIdFormatIdDTO.setCampaignId(campaignIddto.getCampaingId());
//							campaignIdFormatIdDTO.setFormatId(addformatTopicdto.getFormatId());
							
							//eventPublisher.publishEvent(new FormatCreatedEvent(this, campaignIdFormatIdDTO));
							
							createFormatTopicService.createTopicAndSendLog( receivedto.getPipelineId(),campaignIddto.getCampaingId(), addformatTopicdto.getFormatId());

							// 필터토픽 ,컨슈머 생성
							if (addformatTopicdto.getAddFilterTopics() != null) {
								for (AddFilterTopicDTO filterTopicdto : addformatTopicdto.getAddFilterTopics()) {
									
//									CampaignIdFormatIdFilterIdDTO campaignIdFormatIdFilterIdDTO = new CampaignIdFormatIdFilterIdDTO();
//									campaignIdFormatIdFilterIdDTO.setCampaignId(campaignIddto.getCampaingId());
//									campaignIdFormatIdFilterIdDTO.setFormatId(addformatTopicdto.getFormatId());
//									campaignIdFormatIdFilterIdDTO.setFilterId(filterTopicdto.getFilterId());
//									
//									eventPublisher.publishEvent(new FilterCreatedEvent(this, campaignIdFormatIdFilterIdDTO)); //포맷 CampaignIdFormatIdFilterIdDTO
									
									createFilterTopicService.createTopicAndSendLog(receivedto.getPipelineId() ,campaignIddto.getCampaingId(), addformatTopicdto.getFormatId(), filterTopicdto.getFilterId());

								}
							}
						}
					}
				}
				
				updatePipelineStatusService.changePipelineStatus(receivedto.getPipelineId());
		
			
		
		
	}
}
