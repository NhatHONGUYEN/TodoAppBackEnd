package com.example.ToDoApp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          // active CORS avant tout
          .cors(withDefaults())
          // Pas de session ni CSRF (stateless JWT)
          .csrf(csrf -> csrf.disable())
          // Définition des accès
          .authorizeHttpRequests(auth -> auth
              .requestMatchers("/api/tasks/**").authenticated()
              .anyRequest().permitAll()
          )
          // Resource Server JWT
          .oauth2ResourceServer(oauth2 -> oauth2
              .jwt(Customizer.withDefaults())
          );

        return http.build();
    }
}
