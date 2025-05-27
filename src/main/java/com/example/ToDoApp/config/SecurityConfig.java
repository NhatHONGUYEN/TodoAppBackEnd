package com.example.ToDoApp.config;

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

    @SuppressWarnings("removal")
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Configuration CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            // Désactiver CSRF pour les API REST
            .csrf(AbstractHttpConfigurer::disable)
            // Configuration stateless
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Configuration OAuth2/JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter))
            )
            // Configuration des autorisations
            .authorizeHttpRequests(authz -> authz
                // OPTIONS pour CORS preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // URLs Swagger/OpenAPI - IMPORTANT: ordre des patterns
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                
                // Console H2 pour développement
                .requestMatchers("/h2-console/**").permitAll()
                
                // Endpoints publics (si vous en avez)
                .requestMatchers("/api/public/**").permitAll()
                
                // API Tasks - authentification requise
                .requestMatchers("/api/tasks/**").authenticated()
                
                // Autres APIs avec rôles
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                
                // Tout le reste nécessite authentification
                .anyRequest().authenticated()
            )
            // Headers pour H2 Console
            .headers(headers -> headers
                .frameOptions().disable()
                .httpStrictTransportSecurity().disable()
            );

        return http.build();
    }
}