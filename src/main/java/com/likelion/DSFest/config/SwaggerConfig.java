package com.likelion.DSFest.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("2024 근화제 웹페이지 API")
                .description("2024 근화제 '찬란' 웹사이트 API 명세서")
                .version("1.0.0");

        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");

        Server testServer = new Server();
        testServer.setUrl("https://likelion-ds.site"); // https에 접근 가능하게 설정

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, testServer));

    }
}
