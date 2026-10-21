package com.northstar.crm.service;

import com.northstar.crm.domain.*; import com.northstar.crm.repo.*; import com.northstar.crm.security.*;
import org.springframework.security.access.AccessDeniedException; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate; import java.util.*; import static com.northstar.crm.domain.DomainTypes.*;

@Service
public class OrganizationService {
    public record OrganizationInput(String name,String plan,OrganizationStatus status,Health health,int healthScore,LocalDate renewalDate,String billingEmail,String riskSummary){}
    private static final Set<String> PLANS=Set.of("Launch","Scale","Enterprise");
    private final OrganizationRepository organizations; private final EscalationRepository escalations; private final ActorResolver actors; private final AccessPolicy access; private final AuditService audit;
    public OrganizationService(OrganizationRepository organizations,EscalationRepository escalations,ActorResolver actors,AccessPolicy access,AuditService audit){this.organizations=organizations;this.escalations=escalations;this.actors=actors;this.access=access;this.audit=audit;}
    @Transactional(readOnly=true) public List<Organization> search(String query){
        var actor=actors.current();var q=query==null?"":query.strip();List<Organization> result=switch(actor.getRole()){
            case PLATFORM_ADMIN->organizations.searchAll(q); case ACCOUNT_MANAGER->organizations.searchOwned(actor.getId(),q);
            case CUSTOMER_CONTACT->actor.getOrganizationId()==null?List.of():organizations.findById(actor.getOrganizationId()).filter(o->o.getName().toLowerCase().contains(q.toLowerCase())).stream().toList();
            case SUPPORT_AGENT-> {var ids=escalations.findByAssignedAgentIdOrTeamIdOrderByCreatedAtDesc(actor.getId(),actor.getTeamId()).stream().map(Escalation::getOrganizationId).collect(java.util.stream.Collectors.toSet());yield organizations.findAllById(ids).stream().filter(o->o.getName().toLowerCase().contains(q.toLowerCase())).toList();}
        };return result;
    }
    /** Ownership/scope authorization deliberately lives one call frame above controllers/views. */
    @Transactional(readOnly=true) public Organization getAuthorized(UUID id){var actor=actors.current();var org=organizations.findById(id).orElseThrow(()->new NoSuchElementException("Organization not found"));if(actor.getRole()==Role.SUPPORT_AGENT){var assigned=escalations.findByAssignedAgentIdOrTeamIdOrderByCreatedAtDesc(actor.getId(),actor.getTeamId()).stream().anyMatch(e->e.getOrganizationId().equals(id));if(!assigned)throw new AccessDeniedException("Organization is not assigned to your team");}else access.requireOrganization(actor,org);return org;}
    @Transactional public Organization create(OrganizationInput input){var actor=actors.current();if(actor.getRole()!=Role.PLATFORM_ADMIN&&actor.getRole()!=Role.ACCOUNT_MANAGER)throw new AccessDeniedException("Staff access required");validate(input);var org=organizations.save(new Organization(input.name().strip(),input.plan(),input.status(),input.health(),input.healthScore(),actor.getId(),input.renewalDate(),input.billingEmail().toLowerCase(),input.riskSummary().strip()));audit.record(actor,"ORGANIZATION_CREATED","Organization",org.getId(),"{}","name="+org.getName(),"role="+actor.getRole());return org;}
    @Transactional public Organization update(UUID id,OrganizationInput input){var actor=actors.current();var org=organizations.findById(id).orElseThrow();access.requireManageOrganization(actor,org);validate(input);var before="status="+org.getStatus()+",health="+org.getHealthScore();org.update(input.name().strip(),input.plan(),input.status(),input.health(),input.healthScore(),input.renewalDate(),input.billingEmail().toLowerCase(),input.riskSummary().strip());audit.record(actor,"ORGANIZATION_UPDATED","Organization",id,before,"status="+org.getStatus()+",health="+org.getHealthScore(),"owner or platform admin");return org;}
    @Transactional public void delete(UUID id){var actor=actors.current();access.requireAdmin(actor);var org=organizations.findById(id).orElseThrow();organizations.delete(org);audit.record(actor,"ORGANIZATION_DELETED","Organization",id,"name="+org.getName(),"{}","platform admin");}
    private void validate(OrganizationInput i){if(i.name()==null||i.name().isBlank()||i.name().length()>120)throw new IllegalArgumentException("Name is required and must be under 120 characters");if(!PLANS.contains(i.plan()))throw new IllegalArgumentException("Unknown plan");if(i.healthScore()<0||i.healthScore()>100)throw new IllegalArgumentException("Health score must be 0–100");if(i.renewalDate()==null)throw new IllegalArgumentException("Renewal date is required");if(i.billingEmail()==null||!i.billingEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))throw new IllegalArgumentException("Valid billing email required");if(i.riskSummary()==null||i.riskSummary().length()>500)throw new IllegalArgumentException("Risk summary must be under 500 characters");}
}
