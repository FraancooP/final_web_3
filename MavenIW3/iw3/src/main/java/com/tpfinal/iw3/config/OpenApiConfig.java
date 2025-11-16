package com.tpfinal.iw3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * Configuración de OpenAPI/Swagger para documentación interactiva de la API.
 * 
 * Características:
 * - Autenticación JWT configurada (Bearer Token)
 * - Documentación accesible en: http://localhost:8081/swagger-ui.html
 * - API Docs JSON en: http://localhost:8081/v3/api-docs
 * 
 * Los endpoints protegidos mostrarán un candado y requerirán el token JWT.
 * Los endpoints públicos (login, CLI2, CLI3) no requerirán autenticación.
 */
@Configuration
@OpenAPIDefinition(
    security = @SecurityRequirement(name = "Bearer Authentication")
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    in = SecuritySchemeIn.HEADER,
    description = "Ingrese el token JWT obtenido del endpoint /api/v1/auth/login (sin la palabra 'Bearer')"
)
public class OpenApiConfig {
    
    /**
     * Configuración de metadatos de la API.
     * Doc: https://springdoc.org/#Introduction
     */
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Backend TP Final - Ingeniería Web III")
                        .description("API REST para gestión de órdenes de carga de combustible con integración CLI1, CLI2 y CLI3")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Equipo TP Final")
                                .url("https://github.com/FraancooP/final_web_3")
                                .email("contacto@tpfinal.com"))
                        .termsOfService("TOC")
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                );
    }
}
