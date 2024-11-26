package com.wnsud9771.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class FilteringEvent extends ApplicationEvent{
	private static final long serialVersionUID = 1L;

	private final String pipelineId;
	private final String filterTopic;
	private final Long distinctCode;

	public FilteringEvent(Object source,String pipelineId, String filterTopic,Long distinctCode ) {
		super(source);
		this.pipelineId = pipelineId;
		this.filterTopic = filterTopic;
		this.distinctCode = distinctCode;
	}
}
