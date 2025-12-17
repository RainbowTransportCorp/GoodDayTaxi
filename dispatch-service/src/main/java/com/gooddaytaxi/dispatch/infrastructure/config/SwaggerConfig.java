package com.gooddaytaxi.dispatch.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI dispatchOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GoodDay Taxi - Dispatch API")
                        .description("""
                        GoodDay Taxi 배차(Dispatch) 서비스 외부 API 문서입니다.

                        Swagger 테스트용 인증 방식:
                        - X-User-UUID
                        - X-User-Role
                        """)
                        .version("v1.0.0")
                )
                .components(new Components()
                        .addSecuritySchemes("userUuid", new SecurityScheme()
                                .name("X-User-UUID")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                        )
                        .addSecuritySchemes("userRole", new SecurityScheme()
                                .name("X-User-Role")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                        )
                )
                .addSecurityItem(new SecurityRequirement()
                        .addList("userUuid")
                        .addList("userRole")
                );
    }


    /**
     * Passenger API 그룹
     */
    @Bean
    public GroupedOpenApi passengerApi() {
        return GroupedOpenApi.builder()
                .group("Passenger API")
                .pathsToMatch(
                        "/api/v1/dispatches",
                        "/api/v1/dispatches/**"
                )
                .pathsToExclude(
                        "/api/v1/dispatches/driver/**",
                        "/api/v1/admin/dispatches/**"
                )
                .build();
    }

    /**
     * Driver API 그룹
     */
    @Bean
    public GroupedOpenApi driverApi() {
        return GroupedOpenApi.builder()
                .group("Driver API")
                .pathsToMatch("/api/v1/dispatches/driver/**")
                .build();
    }

    /**
     * Admin API 그룹
     */
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("Admin API")
                .pathsToMatch("/api/v1/admin/dispatches/**")
                .build();
    }
}