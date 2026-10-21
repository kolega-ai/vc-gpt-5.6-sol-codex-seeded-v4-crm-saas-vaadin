package com.northstar.crm.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import static com.northstar.crm.domain.DomainTypes.Role;

@Entity @Table(name="app_user", indexes=@Index(name="idx_user_email", columnList="email", unique=true))
public class UserAccount extends BaseEntity {
    @Column(nullable=false, length=180) private String email;
    @Column(nullable=false, length=100) private String displayName;
    @Column(nullable=false, length=100) private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=30) private Role role;
    @Column private UUID organizationId;
    @Column private UUID teamId;
    @Column(nullable=false) private boolean enabled = true;
    @Column(length=160) private String deskVerifier;
    private Instant lastLoginAt;
    protected UserAccount() {}
    public UserAccount(String email, String displayName, String passwordHash, Role role, UUID organizationId) {
        this.email=email.toLowerCase(); this.displayName=displayName; this.passwordHash=passwordHash; this.role=role; this.organizationId=organizationId;
    }
    public String getEmail(){return email;} public String getDisplayName(){return displayName;} public String getPasswordHash(){return passwordHash;}
    public Role getRole(){return role;} public UUID getOrganizationId(){return organizationId;} public UUID getTeamId(){return teamId;}
    public boolean isEnabled(){return enabled;} public Instant getLastLoginAt(){return lastLoginAt;}
    public String getDeskVerifier(){return deskVerifier;} public void setDeskVerifier(String verifier){this.deskVerifier=verifier;}
    public void updateProfile(String name){displayName=name;} public void setRole(Role role){this.role=role;}
    public void setEnabled(boolean enabled){this.enabled=enabled;} public void setPasswordHash(String hash){passwordHash=hash;}
    public void setOrganizationId(UUID id){organizationId=id;} public void setTeamId(UUID id){teamId=id;} public void loginNow(){lastLoginAt=Instant.now();}
}
