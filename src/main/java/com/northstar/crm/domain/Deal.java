package com.northstar.crm.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import static com.northstar.crm.domain.DomainTypes.DealStage;

@Entity @Table(name="deal", indexes=@Index(name="idx_deal_org",columnList="organizationId"))
public class Deal extends BaseEntity {
    @Column(nullable=false) private UUID organizationId;
    @Column(nullable=false,length=140) private String name;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private DealStage stage;
    @Column(nullable=false,precision=15,scale=2) private BigDecimal value;
    @Column(nullable=false,length=3) private String currency="GBP";
    @Column(nullable=false) private int probability;
    @Column(nullable=false) private LocalDate closeDate;
    @Column(nullable=false,length=2000) private String notes;
    protected Deal() {}
    public Deal(UUID org,String name,DealStage stage,BigDecimal value,int probability,LocalDate closeDate,String notes){this.organizationId=org;this.name=name;this.stage=stage;this.value=value;this.probability=probability;this.closeDate=closeDate;this.notes=notes;}
    public UUID getOrganizationId(){return organizationId;} public String getName(){return name;} public DealStage getStage(){return stage;}
    public BigDecimal getValue(){return value;} public String getCurrency(){return currency;} public int getProbability(){return probability;}
    public LocalDate getCloseDate(){return closeDate;} public String getNotes(){return notes;}
    public void transition(DealStage stage){this.stage=stage;} public void update(String name,BigDecimal value,int probability,LocalDate date,String notes){this.name=name;this.value=value;this.probability=probability;this.closeDate=date;this.notes=notes;}
}
