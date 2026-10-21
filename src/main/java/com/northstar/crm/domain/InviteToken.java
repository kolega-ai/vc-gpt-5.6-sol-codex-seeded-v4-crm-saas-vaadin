package com.northstar.crm.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="invite_token")
public class InviteToken extends BaseEntity {
    @Column(nullable=false,unique=true,length=100) private String tokenHash;
    @Column(nullable=false,length=180) private String email;
    @Column(nullable=false) private UUID organizationId;
    @Column(nullable=false) private Instant expiresAt;
    private Instant usedAt;
    protected InviteToken() {}
    public InviteToken(String hash,String email,UUID org,Instant expiry){tokenHash=hash;this.email=email;organizationId=org;expiresAt=expiry;}
    public String getTokenHash(){return tokenHash;} public String getEmail(){return email;} public UUID getOrganizationId(){return organizationId;} public Instant getExpiresAt(){return expiresAt;} public Instant getUsedAt(){return usedAt;} public void use(){usedAt=Instant.now();}
}
