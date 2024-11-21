package com.wnsud9771.event;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationEvent;

import com.wnsud9771.dto.format.CampaignIdFormatIdDTO;

import lombok.Getter;

@Getter
public class FormatCreatedEvent  extends ApplicationEvent{
private static final long serialVersionUID = 1L;
	
	private final String formatId;
	private final String campaignId;
    private final LocalDateTime eventTime;

    public FormatCreatedEvent(Object source,CampaignIdFormatIdDTO campainIdFormatIdDTO) {
        super(source);
        this.campaignId = campainIdFormatIdDTO.getCampaignId();
        this.formatId = campainIdFormatIdDTO.getFormatId();
        this.eventTime = LocalDateTime.now();
    }
}
