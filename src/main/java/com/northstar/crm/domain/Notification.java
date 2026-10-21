package com.northstar.crm.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name="notification",indexes=@Index(name="idx_notification_user",columnList="userId"))
public class Notification extends BaseEntity {
    @Column(nullable=false) private UUID userId;
    @Column(nullable=false,length=120) private String title;
    @Column(nullable=false,length=500) private String message;
    @Column(nullable=false,length=300) private String targetRoute;
    @Column(nullable=false) private boolean readFlag;
    protected Notification() {}
    public Notification(UUID user,String title,String message,String route){this.userId=user;this.title=title;this.message=message;this.targetRoute=route;}
    public UUID getUserId(){return userId;} public String getTitle(){return title;} public String getMessage(){return message;} public String getTargetRoute(){return targetRoute;} public boolean isReadFlag(){return readFlag;} public void markRead(){readFlag=true;}
}
