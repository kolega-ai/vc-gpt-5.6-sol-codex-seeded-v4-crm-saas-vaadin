package com.northstar.crm.repo;
import com.northstar.crm.domain.EscalationComment; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface CommentRepository extends JpaRepository<EscalationComment,UUID> { List<EscalationComment> findByEscalationIdOrderByCreatedAt(UUID id); List<EscalationComment> findByEscalationIdAndInternalOnlyFalseOrderByCreatedAt(UUID id); }
