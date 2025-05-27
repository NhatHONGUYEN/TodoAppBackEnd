package com.example.ToDoApp.config;

import com.example.ToDoApp.security.JwtAuthConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
            // Utilise la configuration CORS du bean CorsConfig
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            // Désactive CSRF car nous utilisons des tokens JWT
            .csrf(AbstractHttpConfigurer::disable)
            // Configuration stateless pour les API REST
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Configuration OAuth2/JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter))
            )
            // Configuration des autorisations
            .authorizeHttpRequests(authz -> authz
                // TRÈS IMPORTANT : autoriser OPTIONS pour CORS preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Ajout des règles pour /api/tasks (correspond à TaskController)
                .requestMatchers("/api/tasks/**").authenticated()
                // Conservons les règles existantes pour d'autres APIs
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                // Tout le reste nécessite authentification
                .anyRequest().authenticated()
            );

        return http.build();
    }
}