package com.audition.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/actuator/health", "/actuator/info")
                    .permitAll() // Allow unauthenticated access to health and info endpoints
                    .requestMatchers("/actuator/**")
                    .authenticated() // Require authentication for other actuator endpoints
                    .anyRequest().permitAll() // Ensure all other requests are authenticated
            )
            .httpBasic(); // Use HTTP Basic Authentication
        return http.build();
    }
}


