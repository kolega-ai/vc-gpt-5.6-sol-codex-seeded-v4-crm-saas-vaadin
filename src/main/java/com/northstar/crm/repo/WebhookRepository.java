package com.northstar.crm.repo;
import com.northstar.crm.domain.WebhookEndpoint; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface WebhookRepository extends JpaRepository<WebhookEndpoint,UUID> { List<WebhookEndpoint> findByOrganizationIdOrderByName(UUID id); }
