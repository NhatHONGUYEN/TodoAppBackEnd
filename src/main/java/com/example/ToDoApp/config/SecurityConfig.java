package com.example.ToDoApp.config;

import com.example.ToDoApp.security.JwtAuthConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtAuthConverter jwtAuthConverter,
                          CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthConverter = jwtAuthConverter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1) active CORS via ton bean
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            // 2) désactive CSRF si tu fais purement une API REST
            .csrf(AbstractHttpConfigurer::disable)
            // 3) configure la gestion de session (STATELESS pour les API REST)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 4) configure l'authentification OAuth2 Resource Server / JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter))
            )
            // 5) règles d'autorisation
            .authorizeHttpRequests(authz -> authz
                // Autoriser les requêtes OPTIONS sans authentification (important pour CORS)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Routes protégées par rôles
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                // Tout le reste nécessite une authentification
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
