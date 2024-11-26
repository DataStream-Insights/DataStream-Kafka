package com.wnsud9771.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.wnsud9771.dto.campaign.CampaignIdDTO;
import com.wnsud9771.dto.pipeline.add.AddFilterTopicDTO;
import com.wnsud9771.dto.pipeline.add.AddFormatTopicDTO;
import com.wnsud9771.dto.pipeline.add.AddPipelineDTO;
import com.wnsud9771.event.FilteringEvent;
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
									String filterTopic = receivedto.getPipelineId()+addformatTopicdto.getFormatId()+filterTopicdto.getFilterId();
									if(receivedto.getDistinctCode() == null) {
										Long distinctCode = 0L;
										eventPublisher.publishEvent(new FilteringEvent(this, receivedto.getPipelineId(),filterTopic, distinctCode));										
									}else {
										eventPublisher.publishEvent(new FilteringEvent(this, receivedto.getPipelineId(),filterTopic, receivedto.getDistinctCode()));
									}
								}
							}
						}
					}
				}
				
				//컨슈머들 모두 true 되면 pipeline status -> true
				log.info("파이프라인 끝나기 이전 상태: {}" , receivedto.getPipelineId());
				updatePipelineStatusService.changePipelineStatus(receivedto.getPipelineId());
				log.info("파이프라인 끝나고 난후 상태: {}" , receivedto.getPipelineId());
				//토픽 다생성되면 해당 필터링 토픽에서 데이터 꺼내와서 db에 저장
				
		
		
	}
}
