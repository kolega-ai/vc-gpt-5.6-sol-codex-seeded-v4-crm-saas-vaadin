package com.northstar.crm.repo;
import com.northstar.crm.domain.ApiCredential; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface ApiCredentialRepository extends JpaRepository<ApiCredential,UUID> { List<ApiCredential> findByOrganizationIdOrderByCreatedAtDesc(UUID id); Optional<ApiCredential> findByPrefix(String prefix); }
