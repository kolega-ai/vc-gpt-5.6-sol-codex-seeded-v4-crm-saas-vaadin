package com.northstar.crm.domain;

import jakarta.persistence.*;

@Entity @Table(name="outbox_message")
public class OutboxMessage extends BaseEntity {
    @Column(nullable=false,length=180) private String recipient;
    @Column(nullable=false,length=180) private String subject;
    @Column(nullable=false,columnDefinition="text") private String body;
    @Column(nullable=false,length=20) private String status="PENDING";
    protected OutboxMessage() {}
    public OutboxMessage(String recipient,String subject,String body){this.recipient=recipient;this.subject=subject;this.body=body;}
    public String getRecipient(){return recipient;} public String getSubject(){return subject;} public String getBody(){return body;} public String getStatus(){return status;} public void sent(){status="SENT";}
}
