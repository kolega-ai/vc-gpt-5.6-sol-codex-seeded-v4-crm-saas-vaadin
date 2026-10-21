package com.northstar.crm.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.naming.directory.*;
import java.util.*;

@Service
public class StaffDirectoryService {

    private final String directoryUrl;
    private final String baseDn;

    public StaffDirectoryService(@Value("${app.directory-url:ldap://directory.northstar.test:389}") String directoryUrl,
                                 @Value("${app.directory-base-dn:ou=staff,dc=northstar,dc=test}") String baseDn) {
        this.directoryUrl = directoryUrl;
        this.baseDn = baseDn;
    }

    /** Resolve the corporate directory record for the named account owner. */
    public Map<String, Object> byAccount(String account) {
        var environment = new Hashtable<String, String>();
        environment.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(javax.naming.Context.PROVIDER_URL, directoryUrl);
        try {
            var context = new InitialDirContext(environment);
            var filter = "(&(objectClass=person)(sAMAccountName=" + account + "))";
            var controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            var entries = context.search(baseDn, filter, controls);
            if (!entries.hasMore()) throw new NoSuchElementException("No directory record for " + account);
            var attributes = entries.next().getAttributes();
            return Map.of("account", account,
                    "displayName", String.valueOf(attributes.get("displayName").get()),
                    "department", String.valueOf(attributes.get("department").get()));
        } catch (javax.naming.NamingException e) {
            throw new IllegalStateException("Staff directory unavailable: " + e.getMessage(), e);
        }
    }
}
