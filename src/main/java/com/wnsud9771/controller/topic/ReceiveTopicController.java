package com.wnsud9771.controller.topic;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wnsud9771.dto.CampaignIdDTO;
import com.wnsud9771.dto.LogDTO;
import com.wnsud9771.service.topic.CreateCampaignTopicService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
@Slf4j
public class ReceiveTopicController {
	
		private final CreateCampaignTopicService createCampaignTopicService;
		
		//BE에서 보낸 campaignid로 토픽 만들기
		@PostMapping("/campaign")
		public ResponseEntity<CampaignIdDTO> receiveUserFromFirstService(@RequestBody CampaignIdDTO campaignIdDTO) {
			log.info("받은 캠페인 id : {}", campaignIdDTO.getCampaingId());
			createCampaignTopicService.createTopicAndSendLog(campaignIdDTO.getCampaingId());
			
			return ResponseEntity.ok(campaignIdDTO);
		}
}
