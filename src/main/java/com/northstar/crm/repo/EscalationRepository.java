package com.northstar.crm.repo;
import com.northstar.crm.domain.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface EscalationRepository extends JpaRepository<Escalation,UUID> { List<Escalation> findByOrganizationIdOrderByCreatedAtDesc(UUID id); List<Escalation> findByOrganizationIdInOrderByCreatedAtDesc(Collection<UUID> ids); List<Escalation> findByAssignedAgentIdOrTeamIdOrderByCreatedAtDesc(UUID agent,UUID team); }
