package com.northstar.crm.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name="contact", indexes=@Index(name="idx_contact_org",columnList="organizationId"))
public class Contact extends BaseEntity {
    @Column(nullable=false) private UUID organizationId;
    @Column(nullable=false,length=100) private String name;
    @Column(nullable=false,length=180) private String email;
    @Column(length=40) private String phone;
    @Column(length=100) private String title;
    @Column(nullable=false) private boolean billingContact;
    @Column(nullable=false) private boolean portalAccess;
    @Column(length=160) private String portalCodeHash;
    protected Contact() {}
    public Contact(UUID organizationId,String name,String email,String phone,String title,boolean billing,boolean portal){this.organizationId=organizationId;this.name=name;this.email=email.toLowerCase();this.phone=phone;this.title=title;this.billingContact=billing;this.portalAccess=portal;}
    public UUID getOrganizationId(){return organizationId;} public String getName(){return name;} public String getEmail(){return email;}
    public String getPhone(){return phone;} public String getTitle(){return title;} public boolean isBillingContact(){return billingContact;} public boolean isPortalAccess(){return portalAccess;}
    public void update(String name,String phone,String title,boolean billing,boolean portal){this.name=name;this.phone=phone;this.title=title;this.billingContact=billing;this.portalAccess=portal;}
    public String getPortalCodeHash(){return portalCodeHash;} public void setPortalCodeHash(String v){portalCodeHash=v;}
}
