package com.northstar.crm.repo;
import com.northstar.crm.domain.AuditEvent; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface AuditRepository extends JpaRepository<AuditEvent,UUID> { List<AuditEvent> findTop200ByOrderByOccurredAtDesc(); List<AuditEvent> findByResourceTypeAndResourceIdOrderByOccurredAtDesc(String type,UUID id); }
