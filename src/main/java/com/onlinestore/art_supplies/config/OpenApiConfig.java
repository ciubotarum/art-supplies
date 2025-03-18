package com.onlinestore.art_supplies.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        String readmeContent = ReadmeUtil.loadReadme();

        return new OpenAPI()
                .info(new Info()
                        .title("Online Art Supplies Shop API")
                        .description(readmeContent)
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .name("BearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    @Bean
    public OpenApiCustomiser securitySchemasCustomizer() {
        return openApi -> openApi.getPaths().values()
                .forEach(pathItem -> pathItem.readOperations()
                        .forEach(operation -> operation.addSecurityItem(new SecurityRequirement().addList("BearerAuth"))));
    }
}

