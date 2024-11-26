package com.wnsud9771.component;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.wnsud9771.event.FilteringEvent;
import com.wnsud9771.service.filtering.FilteringConsumerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilteringEventListener {
	private final FilteringConsumerService filteringConsumerService;

	@Async
	@EventListener
	public void handleFilteringCreated(FilteringEvent event) {
		try {
			event.getDistinctCode();
			event.getFilterTopic();
			event.getPipelineId();
			filteringConsumerService.setupConsumer(event.getPipelineId(), event.getFilterTopic(),
					event.getDistinctCode());

		} catch (Exception e) {
			log.error("필터링 db 작업 컨슈머 오류{} {}", event.getPipelineId(), e);
			// 에러 처리 로직 추가 가능
		}
	}

}