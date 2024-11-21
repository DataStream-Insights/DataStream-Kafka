package com.wnsud9771.service.mybatis;

import org.springframework.stereotype.Service;

import com.wnsud9771.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateConsumerService {
	private final UserMapper userMapper;
	
	//캠페인 컨슈머 수정
	public void updateCampaignConsumer(String campaignId,boolean activate, String targetTopic) {
		userMapper.updateConsumerByCampaignId(campaignId, activate, targetTopic);
		
	}
	
	public void updateFormatConsumer(String campaignId,String formatId,boolean activate, String targetTopic) {
		userMapper.updateConsumerByCampaignIdAndFormatId(campaignId, formatId, activate, targetTopic);
	}
	
	public void updateFilterConsumer(String formatId,String filterId,boolean activate, String targetTopic) {
		userMapper.updateConsumerByFormatIdAndFilterId(formatId, filterId, activate, targetTopic);
	}
}
