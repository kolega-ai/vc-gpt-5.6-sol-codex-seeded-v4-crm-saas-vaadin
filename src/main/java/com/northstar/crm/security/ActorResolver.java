package com.northstar.crm.security;

import com.northstar.crm.domain.UserAccount;
import com.northstar.crm.repo.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ActorResolver {
    private final UserRepository users; private final SignedSessionTokenService tokens;
    public ActorResolver(UserRepository users,SignedSessionTokenService tokens){this.users=users;this.tokens=tokens;}
    /** Channel identity is converted to a short-lived signed opaque token; decision reloads authority from its own DB. */
    public UserAccount current(){var auth=SecurityContextHolder.getContext().getAuthentication();if(auth==null||!auth.isAuthenticated()||"anonymousUser".equals(auth.getPrincipal()))throw new AccessDeniedException("Authentication required");return resolve(tokens.mint(auth.getName()));}
    public UserAccount resolve(String signedToken){var email=tokens.verify(signedToken);var user=users.findByEmailIgnoreCase(email).orElseThrow(()->new AccessDeniedException("Unknown actor"));if(!user.isEnabled())throw new AccessDeniedException("Account disabled");return user;}
}
