package com.northstar.crm.security;

import com.northstar.crm.repo.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CrmUserDetailsService implements UserDetailsService {
    private final UserRepository users;
    public CrmUserDetailsService(UserRepository users){this.users=users;}
    @Override public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user=users.findByEmailIgnoreCase(email).orElseThrow(()->new UsernameNotFoundException("Invalid credentials"));
        return User.withUsername(user.getEmail()).password(user.getPasswordHash()).roles(user.getRole().name()).disabled(!user.isEnabled()).build();
    }
}
