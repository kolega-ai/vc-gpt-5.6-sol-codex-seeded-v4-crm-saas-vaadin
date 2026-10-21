package com.northstar.crm.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseEntity {
    @Id @Column(nullable=false, updatable=false) private UUID id = UUID.randomUUID();
    @Version private long version;
    @Column(nullable=false, updatable=false) private Instant createdAt = Instant.now();
    @Column(nullable=false) private Instant updatedAt = Instant.now();
    @PreUpdate void touch() { updatedAt = Instant.now(); }
    public UUID getId() { return id; }
    public long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
