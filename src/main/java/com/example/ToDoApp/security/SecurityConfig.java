package com.example.ToDoApp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configure(http)) // Active CORS avec la configuration par défaut
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // pour l'instant tout est ouvert
            );
            // Supprime ou commente cette ligne :
            //.oauth2ResourceServer(oauth2 -> oauth2.jwt()); ❌

        return http.build();
    }
}
