package com.northstar.crm.repo;
import com.northstar.crm.domain.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface DocumentRepository extends JpaRepository<CustomerDocument,UUID> { List<CustomerDocument> findByOrganizationIdOrderByCreatedAtDesc(UUID id); List<CustomerDocument> findByOrganizationIdAndVisibilityOrderByCreatedAtDesc(UUID id,DomainTypes.Visibility visibility); }
