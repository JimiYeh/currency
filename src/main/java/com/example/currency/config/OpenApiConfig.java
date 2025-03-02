package com.example.currency.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI currencyOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("幣別轉換 API")
                        .description("提供幣別轉換相關的 REST API")
                        .version("1.0.0")
                        .contact(new Contact().name("Jimi")));
    }
}