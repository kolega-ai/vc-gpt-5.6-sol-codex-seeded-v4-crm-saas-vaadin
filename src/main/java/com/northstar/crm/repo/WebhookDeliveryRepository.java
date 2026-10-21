package com.northstar.crm.repo;
import com.northstar.crm.domain.WebhookDelivery; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface WebhookDeliveryRepository extends JpaRepository<WebhookDelivery,UUID> { List<WebhookDelivery> findTop20ByWebhookIdOrderByCreatedAtDesc(UUID id); }
