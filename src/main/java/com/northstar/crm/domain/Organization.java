package com.northstar.crm.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
import static com.northstar.crm.domain.DomainTypes.*;

@Entity @Table(name="organization")
public class Organization extends BaseEntity {
    @Column(nullable=false,length=120) private String name;
    @Column(nullable=false,length=50) private String plan;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private OrganizationStatus status;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private Health health;
    @Column(nullable=false) private int healthScore;
    @Column(nullable=false) private UUID ownerId;
    @Column(nullable=false) private LocalDate renewalDate;
    @Column(nullable=false,length=180) private String billingEmail;
    @Column(nullable=false,length=500) private String riskSummary;
    protected Organization() {}
    public Organization(String name,String plan,OrganizationStatus status,Health health,int score,UUID ownerId,LocalDate renewal,String billingEmail,String risk){
        this.name=name;this.plan=plan;this.status=status;this.health=health;this.healthScore=score;this.ownerId=ownerId;this.renewalDate=renewal;this.billingEmail=billingEmail;this.riskSummary=risk;
    }
    public String getName(){return name;} public String getPlan(){return plan;} public OrganizationStatus getStatus(){return status;}
    public Health getHealth(){return health;} public int getHealthScore(){return healthScore;} public UUID getOwnerId(){return ownerId;}
    public LocalDate getRenewalDate(){return renewalDate;} public String getBillingEmail(){return billingEmail;} public String getRiskSummary(){return riskSummary;}
    public void update(String name,String plan,OrganizationStatus status,Health health,int score,LocalDate renewal,String billingEmail,String risk){
        this.name=name;this.plan=plan;this.status=status;this.health=health;this.healthScore=score;this.renewalDate=renewal;this.billingEmail=billingEmail;this.riskSummary=risk;
    }
    public void reassignTo(UUID owner){ this.ownerId=owner; }
}
