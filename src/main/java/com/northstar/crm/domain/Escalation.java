package com.northstar.crm.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import static com.northstar.crm.domain.DomainTypes.*;

@Entity @Table(name="escalation", indexes={@Index(name="idx_esc_org",columnList="organizationId"),@Index(name="idx_esc_assignee",columnList="assignedAgentId")})
public class Escalation extends BaseEntity {
    @Column(nullable=false) private UUID organizationId;
    @Column(nullable=false,length=160) private String subject;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private Severity severity;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private EscalationStatus status;
    private UUID assignedAgentId;
    private UUID teamId;
    @Column(nullable=false,length=2000) private String customerSummary;
    @Column(nullable=false) private Instant statusChangedAt=Instant.now();
    protected Escalation() {}
    public Escalation(UUID org,String subject,Severity severity,UUID agent,UUID team,String summary){this.organizationId=org;this.subject=subject;this.severity=severity;this.assignedAgentId=agent;this.teamId=team;this.customerSummary=summary;this.status=EscalationStatus.OPEN;}
    public UUID getOrganizationId(){return organizationId;} public String getSubject(){return subject;} public Severity getSeverity(){return severity;}
    public EscalationStatus getStatus(){return status;} public UUID getAssignedAgentId(){return assignedAgentId;} public UUID getTeamId(){return teamId;}
    public String getCustomerSummary(){return customerSummary;} public Instant getStatusChangedAt(){return statusChangedAt;}
    public void transition(EscalationStatus next){status=next;statusChangedAt=Instant.now();} public void assign(UUID agent,UUID team){assignedAgentId=agent;teamId=team;}
    public void updateSummary(String summary){customerSummary=summary;}
}
