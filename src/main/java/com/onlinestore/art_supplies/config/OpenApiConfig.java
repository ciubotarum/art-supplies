package com.onlinestore.art_supplies.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
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
                        .version("1.0.0"));
    }
}

