package com.lubricentro.backend.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI lubricentroOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Proyecto Lubricentro - API Endpoint List")
                        .description("REST API documentation for the " +
                                "Lubricentro management system.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo Lubricentro")
                                .email("contacto@lubricentro.com")
                                .url("https://lubricentro.com"))
                        .license(new License().name("Apache 2.0")
                                .url("https://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Project GitHub Repository")
                        .url("https://github.com/usuario/proyecto-lubricentro"));
    }
}