package com.example.ToDoApp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;

    public SecurityConfig(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          // use the WebConfig‐defined CorsConfigurationSource by passing a Customizer
          .cors(Customizer.withDefaults())

          // disable CSRF
          .csrf(csrf -> csrf.disable())

          // require authentication for every request
          .authorizeHttpRequests(authorize -> authorize
              .anyRequest().authenticated()
          )

          // configure JWT-based resource server
          .oauth2ResourceServer(oauth2 -> oauth2
              .jwt(jwt -> jwt
                  .jwtAuthenticationConverter(jwtAuthConverter)
              )
          );

        return http.build();
    }
}
