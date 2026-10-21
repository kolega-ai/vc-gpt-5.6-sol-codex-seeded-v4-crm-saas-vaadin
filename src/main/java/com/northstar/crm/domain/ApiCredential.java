package com.northstar.crm.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import static com.northstar.crm.domain.DomainTypes.ApiKeyStatus;

@Entity @Table(name="api_credential",indexes=@Index(name="idx_key_org",columnList="organizationId"))
public class ApiCredential extends BaseEntity {
    @Column(nullable=false) private UUID organizationId;
    @Column(nullable=false,length=100) private String label;
    @Column(nullable=false,length=20) private String prefix;
    @Column(nullable=false,length=100) private String secretHash;
    @Column(nullable=false,length=300) private String scopes;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private ApiKeyStatus status;
    private Instant lastUsedAt;
    private Instant expiresAt;
    protected ApiCredential() {}
    public ApiCredential(UUID org,String label,String prefix,String hash,String scopes,Instant expires){this.organizationId=org;this.label=label;this.prefix=prefix;this.secretHash=hash;this.scopes=scopes;this.status=ApiKeyStatus.ACTIVE;this.expiresAt=expires;}
    public UUID getOrganizationId(){return organizationId;} public String getLabel(){return label;} public String getPrefix(){return prefix;} public String getSecretHash(){return secretHash;}
    public String getScopes(){return scopes;} public ApiKeyStatus getStatus(){return status;} public Instant getLastUsedAt(){return lastUsedAt;} public Instant getExpiresAt(){return expiresAt;}
    public void revoke(){status=ApiKeyStatus.REVOKED;} public void expire(){status=ApiKeyStatus.EXPIRED;} public void used(){lastUsedAt=Instant.now();}
}
