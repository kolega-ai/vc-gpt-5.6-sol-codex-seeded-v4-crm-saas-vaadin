package com.northstar.crm.repo;
import com.northstar.crm.domain.Contact; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.util.*;
public interface ContactRepository extends JpaRepository<Contact,UUID> {
 List<Contact> findByOrganizationIdOrderByName(UUID organizationId);
 Optional<Contact> findFirstByEmailIgnoreCase(String email);
 @Query("select c from Contact c where c.organizationId in :orgs and (lower(c.name) like lower(concat('%',:q,'%')) or lower(c.email) like lower(concat('%',:q,'%'))) order by c.name") List<Contact> scopedSearchByName(@Param("orgs") Collection<UUID> orgs,@Param("q") String q);
 @Query("select c from Contact c where c.organizationId in :orgs and (lower(c.name) like lower(concat('%',:q,'%')) or lower(c.email) like lower(concat('%',:q,'%'))) order by c.email") List<Contact> scopedSearchByEmail(@Param("orgs") Collection<UUID> orgs,@Param("q") String q);
}
