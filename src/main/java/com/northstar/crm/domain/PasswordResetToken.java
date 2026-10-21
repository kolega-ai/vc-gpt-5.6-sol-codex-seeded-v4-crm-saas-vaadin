package com.northstar.crm.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="password_reset_token")
public class PasswordResetToken extends BaseEntity {
    @Column(nullable=false,unique=true,length=100) private String tokenHash;
    @Column(nullable=false) private UUID userId;
    @Column(nullable=false) private Instant expiresAt;
    private Instant usedAt;
    protected PasswordResetToken() {}
    public PasswordResetToken(String hash,UUID user,Instant expiry){tokenHash=hash;userId=user;expiresAt=expiry;}
    public String getTokenHash(){return tokenHash;} public UUID getUserId(){return userId;} public Instant getExpiresAt(){return expiresAt;} public Instant getUsedAt(){return usedAt;} public void use(){usedAt=Instant.now();}
}
