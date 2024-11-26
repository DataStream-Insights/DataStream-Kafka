package com.wnsud9771.event;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationEvent;

import com.wnsud9771.dto.filter.filtering.CampaignIdFormatIdFilterIdDTO;

import lombok.Getter;

@Getter
public class FilterCreatedEvent extends ApplicationEvent{
	private static final long serialVersionUID = 1L;

	private final String formatId;
	private final String filterId;
	private final String campaignId;
	private final LocalDateTime eventTime;

	public FilterCreatedEvent(Object source, CampaignIdFormatIdFilterIdDTO dto) {
		super(source);
		this.formatId = dto.getFormatId();
		this.filterId = dto.getFilterId();
		this.campaignId = dto.getCampaignId();
		this.eventTime = LocalDateTime.now();
	}
}
