package com.example.ToDoApp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${auth.keycloak.server-url:https://todoappkeycloak-production.up.railway.app}")
    private String keycloakServerUrl;
    
    @Value("${auth.keycloak.realm:todo-backend}")
    private String keycloakRealm;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "OAuth2";
        
        String authorizationUrl = keycloakServerUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/auth";
        String tokenUrl = keycloakServerUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/token";
        
        return new OpenAPI()
                .info(new Info()
                        .title("ToDo API")
                        .description("API pour l'application ToDo")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Développeur")
                                .email("contact@example.com")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl(authorizationUrl)
                                                .tokenUrl(tokenUrl)
                                                .scopes(new io.swagger.v3.oas.models.security.Scopes()
                                                        .addString("openid", "OpenID Connect")
                                                        .addString("profile", "Profile information")
                                                        .addString("email", "Email information"))))));
    }
}
