package com.wnsud9771.service.mybatis;

import org.springframework.stereotype.Service;

import com.wnsud9771.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateConsumerService {
	private final UserMapper userMapper;
	
	//캠페인 컨슈머 수정
	public void updateCampaignConsumer(String pipelineId,String campaignId,boolean activate, String targetTopic) {
		userMapper.updateCampaignTopicConsumerAndName(pipelineId,campaignId, activate, targetTopic);
		
	}
	
	public void updateFormatConsumer(String pipelineId, String campaignId,String formatId,boolean activate, String targetTopic) {
		userMapper.updateFormatTopicConsumerAndName(pipelineId,campaignId, formatId, activate, targetTopic);
	}
	
	public void updateFilterConsumer(String pipelineId, String formatId,String filterId,boolean activate, String targetTopic) {
		userMapper.updateFilterTopicConsumerAndName(pipelineId, formatId, filterId, activate, targetTopic);
	}
	
	public void stopCampaignConsumer(String pipelineId, String campaignId, boolean activate) {
		userMapper.updateCampaignTopicConsumer(pipelineId,campaignId, activate);
		
	}
	
	public void stopFormatConsumer(String pipelineId, String campaignId,String formatId,boolean activate) {
		userMapper.updateFormatTopicConsumer(pipelineId,campaignId, formatId, activate);
	}
	
	public void stopFilterConsumer(String pipelineId, String formatId,String filterId,boolean activate) {
		userMapper.updateFilterTopicConsumer(pipelineId,formatId, filterId, activate);
	}
}
