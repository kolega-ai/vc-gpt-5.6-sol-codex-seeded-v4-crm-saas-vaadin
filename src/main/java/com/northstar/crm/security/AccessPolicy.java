package com.northstar.crm.security;

import com.northstar.crm.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import java.util.*;
import static com.northstar.crm.domain.DomainTypes.*;

@Component
public class AccessPolicy {
    public void requireAdmin(UserAccount actor){if(actor.getRole()!=Role.PLATFORM_ADMIN)deny();}
    public void requireStaff(UserAccount actor){if(actor.getRole()==Role.CUSTOMER_CONTACT)deny();}
    public boolean canAccessOrganization(UserAccount actor,Organization org){return switch(actor.getRole()){case PLATFORM_ADMIN->true;case ACCOUNT_MANAGER->org.getOwnerId().equals(actor.getId());case SUPPORT_AGENT->true;case CUSTOMER_CONTACT->Objects.equals(actor.getOrganizationId(),org.getId());};}
    public void requireOrganization(UserAccount actor,Organization org){if(!canAccessOrganization(actor,org))deny();}
    public void requireManageOrganization(UserAccount actor,Organization org){if(actor.getRole()!=Role.PLATFORM_ADMIN && !(actor.getRole()==Role.ACCOUNT_MANAGER&&org.getOwnerId().equals(actor.getId())))deny();}
    public boolean canAccessEscalation(UserAccount actor,Escalation e){return switch(actor.getRole()){case PLATFORM_ADMIN->true;case ACCOUNT_MANAGER->true;case SUPPORT_AGENT->Objects.equals(actor.getId(),e.getAssignedAgentId())||Objects.equals(actor.getTeamId(),e.getTeamId());case CUSTOMER_CONTACT->Objects.equals(actor.getOrganizationId(),e.getOrganizationId());};}
    public void requireEscalation(UserAccount actor,Escalation e){if(!canAccessEscalation(actor,e))deny();}
    public void requireDocument(UserAccount actor,CustomerDocument d){if(actor.getRole()==Role.CUSTOMER_CONTACT&&(!Objects.equals(actor.getOrganizationId(),d.getOrganizationId())||d.getVisibility()!=Visibility.CUSTOMER_VISIBLE))deny();}
    public void requireApiManagement(UserAccount actor,Organization org){if(actor.getRole()!=Role.PLATFORM_ADMIN&&!(actor.getRole()==Role.ACCOUNT_MANAGER&&org.getOwnerId().equals(actor.getId())))deny();}
    private void deny(){throw new AccessDeniedException("You do not have access to this record");}
}
