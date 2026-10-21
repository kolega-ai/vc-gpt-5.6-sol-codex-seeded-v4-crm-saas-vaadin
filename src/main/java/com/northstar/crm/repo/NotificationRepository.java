package com.northstar.crm.repo;
import com.northstar.crm.domain.Notification; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface NotificationRepository extends JpaRepository<Notification,UUID> { List<Notification> findTop30ByUserIdOrderByCreatedAtDesc(UUID id); long countByUserIdAndReadFlagFalse(UUID id); }
