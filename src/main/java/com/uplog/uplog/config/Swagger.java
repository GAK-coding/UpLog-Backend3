package com.uplog.uplog.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class Swagger {

    @Bean
    public OpenAPI openAPI() {

        Info info = new Info()
                .version("v1.0.0")
                .title("타이틀")
                .description("Description");

        // SecuritySecheme명
        String jwtSchemeName = "jwtAuth";
        // API 요청헤더에 인증정보 포함
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
        // SecuritySchemes 등록
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP) // HTTP 방식
                        .scheme("bearer")
                        .bearerFormat("JWT")); // 토큰 형식을 지정하는 임의의 문자(Optional)

        return new OpenAPI()
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    //도메인 별 컨트롤러끼리 group을 묶든 url에 맞게, 용도에 맞게 그룹으로 묶으면 될듯

    //member
    @Bean
    public GroupedOpenApi memberOpenApi() {
        String[] paths = {"/members/**"};

        return GroupedOpenApi.builder()
                .group("members")
                .pathsToMatch(paths)
                .build();
    }
    //task
    @Bean
    public GroupedOpenApi TaskOpenApi() {
        String[] paths = {"/tasks/**"};

        return GroupedOpenApi.builder()
                .group("tasks")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi PostOpenApi() {
        String[] paths = {"/posts/**"};

        return GroupedOpenApi.builder()
                .group("posts")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi commentOpenApi(){
        String[] paths = {"/comment/**"};

        return GroupedOpenApi.builder()
                .group("comment")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi projectOpenApi(){
        String[] paths = {"/projects/**"};

        return GroupedOpenApi.builder()
                .group("projects")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi changedIssueOpenApi(){
        String[] paths = {"/changedIssues/**"};

        return GroupedOpenApi.builder()
                .group("changedIssues")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi productOpenApi(){
        String[] paths = {"/products/**"};

        return GroupedOpenApi.builder()
                .group("products")
                .pathsToMatch(paths)
                .build();
    }
}
