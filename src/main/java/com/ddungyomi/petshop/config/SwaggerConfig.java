package com.ddungyomi.petshop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Petshop API")
                        .description("뚱이요미 샵(굿즈 쇼핑몰) 백엔드 API")
                        .version("v0.0.1"));
    }
}
