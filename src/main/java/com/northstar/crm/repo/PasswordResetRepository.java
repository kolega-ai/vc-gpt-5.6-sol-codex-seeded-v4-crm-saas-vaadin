package com.northstar.crm.repo;
import com.northstar.crm.domain.PasswordResetToken; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface PasswordResetRepository extends JpaRepository<PasswordResetToken,UUID> { Optional<PasswordResetToken> findByTokenHash(String hash); }
