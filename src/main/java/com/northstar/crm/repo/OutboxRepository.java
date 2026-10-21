package com.northstar.crm.repo;
import com.northstar.crm.domain.OutboxMessage; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface OutboxRepository extends JpaRepository<OutboxMessage,UUID> { List<OutboxMessage> findTop50ByStatusOrderByCreatedAt(String status); }
