package com.northstar.crm.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name="webhook_endpoint",indexes=@Index(name="idx_hook_org",columnList="organizationId"))
public class WebhookEndpoint extends BaseEntity {
    @Column(nullable=false) private UUID organizationId;
    @Column(nullable=false,length=120) private String name;
    @Column(nullable=false,length=500) private String url;
    @Column(nullable=false,length=500) private String events;
    @Column(nullable=false,length=100) private String secretHash;
    @Column(nullable=false) private boolean enabled=true;
    protected WebhookEndpoint() {}
    public WebhookEndpoint(UUID org,String name,String url,String events,String secretHash){this.organizationId=org;this.name=name;this.url=url;this.events=events;this.secretHash=secretHash;}
    public UUID getOrganizationId(){return organizationId;} public String getName(){return name;} public String getUrl(){return url;} public String getEvents(){return events;} public String getSecretHash(){return secretHash;} public boolean isEnabled(){return enabled;}
    public void update(String name,String url,String events,boolean enabled){this.name=name;this.url=url;this.events=events;this.enabled=enabled;}
}
