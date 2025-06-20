package com.example.ToDoApp.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        // Extraction du sujet (userId) du token
        String username = jwt.getSubject();
        
        // Extraction des rôles depuis le token (si présents)
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        
        // Création du token d'authentification
        return new JwtAuthenticationToken(jwt, authorities, username);
    }
    
    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        // 1) Realm roles
        Map<String,Object> realmAccess = jwt.getClaim("realm_access");
        Collection<String> realmRoles = realmAccess != null
            ? (Collection<String>) realmAccess.get("roles")
            : Collections.emptyList();

        // 2) Client roles
        Map<String,Object> resourceAccess = jwt.getClaim("resource_access");
        Collection<String> clientRoles = Collections.emptyList();
        if (resourceAccess != null) {
            Map<String,Object> client = 
                (Map<String,Object>) resourceAccess.get("todo-backend-local");
            if (client != null) {
                clientRoles = (Collection<String>) client.get("roles");
            }
        }

        // Fusionner tous les rôles
        Collection<String> allRoles = Stream.concat(realmRoles.stream(), clientRoles.stream())
            .collect(Collectors.toList());

        // Si l'utilisateur n'a que les rôles par défaut de Keycloak, lui donner USER
        boolean hasOnlyDefaultRoles = allRoles.stream()
            .allMatch(role -> role.equals("offline_access") || 
                             role.equals("uma_authorization") || 
                             role.startsWith("default-roles-"));

        if (hasOnlyDefaultRoles || (!allRoles.contains("USER") && !allRoles.contains("ADMIN"))) {
            // Ajouter USER par défaut pour les utilisateurs GitHub
            allRoles = Stream.concat(allRoles.stream(), Stream.of("USER"))
                .collect(Collectors.toList());
        }

        // Prefixer avec ROLE_
        return allRoles.stream()
            .map(r -> new SimpleGrantedAuthority("ROLE_" + r.toUpperCase()))
            .collect(Collectors.toList());
    }
} 