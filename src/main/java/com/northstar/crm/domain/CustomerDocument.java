package com.northstar.crm.domain;

import jakarta.persistence.*;
import java.util.UUID;
import static com.northstar.crm.domain.DomainTypes.*;

@Entity @Table(name="customer_document",indexes=@Index(name="idx_doc_org",columnList="organizationId"))
public class CustomerDocument extends BaseEntity {
    @Column(nullable=false) private UUID organizationId;
    @Column(nullable=false) private UUID uploadedBy;
    @Column(nullable=false,length=180) private String displayName;
    @Column(nullable=false,length=180) private String storedName;
    @Column(nullable=false,length=120) private String contentType;
    @Column(nullable=false) private long sizeBytes;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=40) private DocumentCategory category;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private Visibility visibility;
    @Column(nullable=false,length=300) private String tags;
    protected CustomerDocument() {}
    public CustomerDocument(UUID org,UUID user,String name,String stored,String type,long size,DocumentCategory category,Visibility visibility,String tags){this.organizationId=org;this.uploadedBy=user;this.displayName=name;this.storedName=stored;this.contentType=type;this.sizeBytes=size;this.category=category;this.visibility=visibility;this.tags=tags;}
    public UUID getOrganizationId(){return organizationId;} public UUID getUploadedBy(){return uploadedBy;} public String getDisplayName(){return displayName;}
    public String getStoredName(){return storedName;} public String getContentType(){return contentType;} public long getSizeBytes(){return sizeBytes;}
    public DocumentCategory getCategory(){return category;} public Visibility getVisibility(){return visibility;} public String getTags(){return tags;}
    public void updateMetadata(String name,DocumentCategory category,Visibility visibility,String tags){displayName=name;this.category=category;this.visibility=visibility;this.tags=tags;}
}
