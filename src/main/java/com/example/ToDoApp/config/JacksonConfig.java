package com.example.ToDoApp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration Jackson pour gérer correctement les dates Java 8 (LocalDate, LocalDateTime)
 * entre le frontend Angular et le backend Spring Boot.
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Ajoute le support pour les types de date Java 8+ (LocalDate, LocalDateTime, etc.)
        objectMapper.registerModule(new JavaTimeModule());
        
        // Désactive l'écriture des dates comme des timestamps (nombres)
        // et utilise plutôt le format ISO par défaut
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return objectMapper;
    }
} 