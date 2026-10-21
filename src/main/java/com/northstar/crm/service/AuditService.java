package com.northstar.crm.service;

import com.northstar.crm.domain.*;
import com.northstar.crm.repo.AuditRepository;
import com.northstar.crm.security.ActorResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import java.util.*;

@Service
public class AuditService {
    private final AuditRepository audits; private final ActorResolver actors;
    public AuditService(AuditRepository audits,ActorResolver actors){this.audits=audits;this.actors=actors;}
    @Transactional(propagation=Propagation.MANDATORY) public void record(UserAccount actor,String action,String type,UUID id,String before,String after,String basis){audits.save(new AuditEvent(actor.getId(),action,type,id,safe(before),safe(after),basis));}
    @Transactional(readOnly=true) public List<AuditEvent> recent(){var actor=actors.current();if(actor.getRole()!=DomainTypes.Role.PLATFORM_ADMIN)throw new org.springframework.security.access.AccessDeniedException("Admin access required");return audits.findTop200ByOrderByOccurredAtDesc();}
    private String safe(String value){if(value==null)return "{}";return value.length()>4000?value.substring(0,4000):value;}
}
