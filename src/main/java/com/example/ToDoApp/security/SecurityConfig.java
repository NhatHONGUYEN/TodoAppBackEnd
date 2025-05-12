package com.example.ToDoApp.security;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          // Pas de session ni CSRF (stateless JWT)
          .csrf(csrf -> csrf.disable())

          // Définition des accès : toutes les requêtes /api/tasks/** doivent être authentifiées
          // Les autres (swagger, health, etc.) sont publiques
          .authorizeHttpRequests(auth -> auth
              .requestMatchers("/api/tasks/**").authenticated()
              .anyRequest().permitAll()
          )

          // On branche le Resource Server JWT (prise en charge OIDC)
          .oauth2ResourceServer(oauth2 -> oauth2
              .jwt(Customizer.withDefaults())
          );

        return http.build();
    }
}
