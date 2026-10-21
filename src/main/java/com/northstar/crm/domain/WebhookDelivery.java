package com.northstar.crm.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name="webhook_delivery",indexes=@Index(name="idx_delivery_hook",columnList="webhookId"))
public class WebhookDelivery extends BaseEntity {
    @Column(nullable=false) private UUID webhookId;
    @Column(nullable=false,length=500) private String requestUrl;
    @Column(nullable=false,length=60) private String eventType;
    @Column(nullable=false,length=30) private String result;
    @Column(nullable=false,length=500) private String responseSummary;
    protected WebhookDelivery() {}
    public WebhookDelivery(UUID webhook,String url,String event,String result,String summary){this.webhookId=webhook;this.requestUrl=url;this.eventType=event;this.result=result;this.responseSummary=summary;}
    public UUID getWebhookId(){return webhookId;} public String getRequestUrl(){return requestUrl;} public String getEventType(){return eventType;} public String getResult(){return result;} public String getResponseSummary(){return responseSummary;}
}
