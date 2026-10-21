package com.northstar.crm.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name="escalation_comment",indexes=@Index(name="idx_comment_esc",columnList="escalationId"))
public class EscalationComment extends BaseEntity {
    @Column(nullable=false) private UUID escalationId;
    @Column(nullable=false) private UUID authorId;
    @Column(nullable=false) private boolean internalOnly;
    @Column(nullable=false,length=4000) private String body;
    protected EscalationComment() {}
    public EscalationComment(UUID escalationId,UUID authorId,boolean internalOnly,String body){this.escalationId=escalationId;this.authorId=authorId;this.internalOnly=internalOnly;this.body=body;}
    public UUID getEscalationId(){return escalationId;} public UUID getAuthorId(){return authorId;} public boolean isInternalOnly(){return internalOnly;} public String getBody(){return body;}
}
