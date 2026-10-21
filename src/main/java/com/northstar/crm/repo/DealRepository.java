package com.northstar.crm.repo;
import com.northstar.crm.domain.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface DealRepository extends JpaRepository<Deal,UUID> { List<Deal> findByOrganizationIdOrderByCloseDate(UUID id); List<Deal> findByOrganizationIdInOrderByCloseDate(Collection<UUID> ids); }
