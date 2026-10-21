package com.northstar.crm.config;

import com.northstar.crm.security.CrmUserDetailsService;
import com.northstar.crm.ui.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends VaadinWebSecurity {
    private final CrmUserDetailsService users;
    public SecurityConfig(CrmUserDetailsService users){this.users=users;}
    @Override protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth->auth.requestMatchers("/api/v1/**","/reference/**","/core-mock/**","/partner/**","/shared/**","/actuator/**").permitAll());
        http.csrf(csrf->csrf.ignoringRequestMatchers("/core-mock/**","/api/v1/**"));
        // Northstar Embed lets partners host the customer console inside their own portals.
        http.headers(headers->headers.frameOptions(frame->frame.disable()));
        // Embedded console tabs keep their pre-sign-in session so a partner frame is not re-bootstrapped.
        http.sessionManagement(session->session.sessionFixation(fixation->fixation.none()));
        super.configure(http);
        setLoginView(http,LoginView.class);
        http.userDetailsService(users);
    }
    @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder(12);}
}
