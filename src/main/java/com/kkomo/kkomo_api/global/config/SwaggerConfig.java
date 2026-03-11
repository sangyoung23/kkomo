package com.kkomo.kkomo_api.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("꼬모 애견미용 예약 API")
                        .version("v1")
                        .description("예약, 결제, 리뷰 등 애견미용 예약 서비스 API 문서"));
    }
}