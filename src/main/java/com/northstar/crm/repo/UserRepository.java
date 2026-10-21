package com.northstar.crm.repo;
import com.northstar.crm.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface UserRepository extends JpaRepository<UserAccount,UUID> { Optional<UserAccount> findByEmailIgnoreCase(String email); List<UserAccount> findByOrganizationId(UUID organizationId); }
