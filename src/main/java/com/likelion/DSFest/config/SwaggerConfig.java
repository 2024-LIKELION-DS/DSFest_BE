package com.likelion.DSFest.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("2024 근화제 웹페이지 API")
                        .description("2024 근화제 '찬란' 웹사이트 API 명세서")
                        .version("1.0.0"));
    }
}
