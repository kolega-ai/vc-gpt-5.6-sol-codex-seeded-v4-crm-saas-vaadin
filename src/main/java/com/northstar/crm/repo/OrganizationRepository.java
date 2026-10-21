package com.northstar.crm.repo;
import com.northstar.crm.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate; import java.util.*;
public interface OrganizationRepository extends JpaRepository<Organization,UUID> {
 List<Organization> findByOwnerIdOrderByRenewalDate(UUID ownerId);
 @Query("select o from Organization o where lower(o.name) like lower(concat('%',:q,'%')) order by o.name") List<Organization> searchAll(@Param("q") String query);
 @Query("select o from Organization o where o.ownerId=:owner and lower(o.name) like lower(concat('%',:q,'%')) order by o.name") List<Organization> searchOwned(@Param("owner") UUID owner,@Param("q") String query);
 List<Organization> findByRenewalDateBetweenOrderByRenewalDate(LocalDate from,LocalDate to);
}
