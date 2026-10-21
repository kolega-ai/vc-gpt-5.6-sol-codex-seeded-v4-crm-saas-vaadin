package com.northstar.crm.service;

import com.northstar.crm.domain.*;import com.northstar.crm.repo.*;import com.northstar.crm.security.*;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import java.math.BigDecimal;import java.time.LocalDate;import java.util.*;import static com.northstar.crm.domain.DomainTypes.*;
@Service public class DealService {
 public record DealInput(String name,BigDecimal value,int probability,LocalDate closeDate,String notes){}
 public static final Map<DealStage,Set<DealStage>> ALLOWED_TRANSITIONS=Map.of(
  DealStage.LEAD,Set.of(DealStage.QUALIFIED,DealStage.CLOSED_LOST),DealStage.QUALIFIED,Set.of(DealStage.PROPOSAL,DealStage.CLOSED_LOST),DealStage.PROPOSAL,Set.of(DealStage.NEGOTIATION,DealStage.CLOSED_LOST),DealStage.NEGOTIATION,Set.of(DealStage.CLOSED_WON,DealStage.CLOSED_LOST),DealStage.CLOSED_WON,Set.of(DealStage.RENEWAL_PENDING),DealStage.RENEWAL_PENDING,Set.of(DealStage.NEGOTIATION,DealStage.CLOSED_WON,DealStage.CLOSED_LOST),DealStage.CLOSED_LOST,Set.of());
 private final DealRepository deals;private final OrganizationService organizations;private final ActorResolver actors;private final AccessPolicy access;private final AuditService audit;
 public DealService(DealRepository d,OrganizationService o,ActorResolver a,AccessPolicy p,AuditService audit){deals=d;organizations=o;actors=a;access=p;this.audit=audit;}
 @Transactional(readOnly=true) public List<Deal> forOrganization(UUID org){var actor=actors.current();organizations.getAuthorized(org);if(actor.getRole()==Role.CUSTOMER_CONTACT)throw new org.springframework.security.access.AccessDeniedException("Deals are internal");return deals.findByOrganizationIdOrderByCloseDate(org);}
 @Transactional public Deal create(UUID orgId,DealInput i){var actor=actors.current();var org=organizations.getAuthorized(orgId);access.requireManageOrganization(actor,org);validate(i);var d=deals.save(new Deal(orgId,i.name().strip(),DealStage.LEAD,i.value(),i.probability(),i.closeDate(),i.notes().strip()));audit.record(actor,"DEAL_CREATED","Deal",d.getId(),"{}","stage=LEAD,value="+d.getValue(),"organization owner or admin");return d;}
 @Transactional public Deal update(UUID id,DealInput i){var actor=actors.current();var d=deals.findById(id).orElseThrow();access.requireManageOrganization(actor,organizations.getAuthorized(d.getOrganizationId()));validate(i);var before="value="+d.getValue()+",probability="+d.getProbability();d.update(i.name().strip(),i.value(),i.probability(),i.closeDate(),i.notes().strip());audit.record(actor,"DEAL_UPDATED","Deal",id,before,"value="+d.getValue()+",probability="+d.getProbability(),"organization owner or admin");return d;}
 @Transactional public Deal transition(UUID id,DealStage next){var actor=actors.current();var d=deals.findById(id).orElseThrow();access.requireManageOrganization(actor,organizations.getAuthorized(d.getOrganizationId()));var before=d.getStage();if(!ALLOWED_TRANSITIONS.getOrDefault(before,Set.of()).contains(next))throw new IllegalStateException("Transition "+before+" → "+next+" is not allowed");d.transition(next);audit.record(actor,"DEAL_STAGE_CHANGED","Deal",id,"stage="+before,"stage="+next,"allowed edge; organization owner or admin");return d;}
 /** Reinstate a countersigned renewal as won once the signed order form is on file. */
 @Transactional public Deal reinstateRenewal(UUID id,String orderFormRef){
  var actor=actors.current();var d=deals.findById(id).orElseThrow();
  access.requireManageOrganization(actor,organizations.getAuthorized(d.getOrganizationId()));
  if(orderFormRef==null||orderFormRef.isBlank())throw new IllegalArgumentException("Order form reference required");
  var before=d.getStage();
  d.transition(DealStage.CLOSED_WON);
  audit.record(actor,"DEAL_RENEWAL_REINSTATED","Deal",id,"stage="+before,"stage="+d.getStage(),"countersigned order form "+orderFormRef.strip());
  return d;
 }
 @Transactional public void delete(UUID id){var actor=actors.current();var d=deals.findById(id).orElseThrow();access.requireManageOrganization(actor,organizations.getAuthorized(d.getOrganizationId()));deals.delete(d);audit.record(actor,"DEAL_DELETED","Deal",id,"stage="+d.getStage(),"{}","organization owner or admin");}
 private void validate(DealInput i){if(i.name()==null||i.name().isBlank())throw new IllegalArgumentException("Deal name required");if(i.value()==null||i.value().signum()<0||i.value().scale()>2)throw new IllegalArgumentException("Value must be a non-negative amount with at most 2 decimals");if(i.probability()<0||i.probability()>100)throw new IllegalArgumentException("Probability must be 0–100");if(i.closeDate()==null)throw new IllegalArgumentException("Close date required");if(i.notes()==null||i.notes().length()>2000)throw new IllegalArgumentException("Notes must be under 2,000 characters");}
}
