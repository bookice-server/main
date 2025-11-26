package com.dgsw.bookice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("도서 관리 시스템 API")
                        .version("1.0.0")
                        .description("Spring Boot 기반 도서 관리 REST API 문서입니다.")
                        .contact(new Contact()
                                .name("DGSW Team")
                                .email("team@dgsw.hs.kr")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("로컬 서버")
                ));
    }
}