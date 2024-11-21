package com.wnsud9771.event;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class CampaignCreatedEvent extends ApplicationEvent{

	private static final long serialVersionUID = 1L;
	
	private final String campaignId;
    private final LocalDateTime eventTime;

    public CampaignCreatedEvent(Object source, String campaignId) {
        super(source);
        this.campaignId = campaignId;
        this.eventTime = LocalDateTime.now();
    }
    
    

}
