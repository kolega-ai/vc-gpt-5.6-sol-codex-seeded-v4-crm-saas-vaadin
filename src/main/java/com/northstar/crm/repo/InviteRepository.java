package com.northstar.crm.repo;
import com.northstar.crm.domain.InviteToken; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface InviteRepository extends JpaRepository<InviteToken,UUID> { Optional<InviteToken> findByTokenHash(String hash); }
