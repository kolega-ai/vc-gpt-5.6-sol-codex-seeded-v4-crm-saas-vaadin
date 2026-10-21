package com.northstar.crm.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="audit_event",indexes={@Index(name="idx_audit_actor",columnList="actorId"),@Index(name="idx_audit_resource",columnList="resourceType,resourceId")})
public class AuditEvent {
    @Id private UUID id=UUID.randomUUID();
    @Column(nullable=false,updatable=false) private Instant occurredAt=Instant.now();
    @Column(nullable=false,updatable=false) private UUID actorId;
    @Column(nullable=false,updatable=false,length=80) private String action;
    @Column(nullable=false,updatable=false,length=80) private String resourceType;
    @Column(updatable=false) private UUID resourceId;
    @Column(nullable=false,updatable=false,columnDefinition="text") private String beforeValue;
    @Column(nullable=false,updatable=false,columnDefinition="text") private String afterValue;
    @Column(nullable=false,updatable=false,length=300) private String authorizationBasis;
    protected AuditEvent() {}
    public AuditEvent(UUID actor,String action,String type,UUID resource,String before,String after,String basis){this.actorId=actor;this.action=action;this.resourceType=type;this.resourceId=resource;this.beforeValue=before;this.afterValue=after;this.authorizationBasis=basis;}
    public UUID getId(){return id;} public Instant getOccurredAt(){return occurredAt;} public UUID getActorId(){return actorId;} public String getAction(){return action;}
    public String getResourceType(){return resourceType;} public UUID getResourceId(){return resourceId;} public String getBeforeValue(){return beforeValue;} public String getAfterValue(){return afterValue;} public String getAuthorizationBasis(){return authorizationBasis;}
}
