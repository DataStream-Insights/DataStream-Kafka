package com.wnsud9771.component;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.wnsud9771.event.FilterCreatedEvent;
import com.wnsud9771.service.mybatis.MybatisService;
import com.wnsud9771.service.topic.CreateFilterTopicService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilterEventListener {
	private final CreateFilterTopicService createFilterTopicService;
	private final MybatisService mybatisService;

	@Async
	@EventListener
	public void handleFormatCreated(FilterCreatedEvent event) {
		try {

			// 필터 토픽생성
			// 해당 핕러 토픽 + 컨슈밍그룹아이디(필터아이디+포맷아이디) 로해서 컨슈머생성, 걔네 로그 파싱
			// 파싱된 결과 값들-> 필터토픽으로 프로듀싱
			if(!createFilterTopicService.createTopicAndSendLog(event.getCampaignId() ,event.getFilterId(), event.getFormatId())) {
				//필터 토픽 제대로 생성 안되면 db record에 다시 집어넣음
				
				return ;
			}

		} catch (Exception e) {
			log.error("캠페인 오프셋 db 오류", event.getFormatId(), e);
			// 에러 처리 로직 추가 가능
		}
	}
}
