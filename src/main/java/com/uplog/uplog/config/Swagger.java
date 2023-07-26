package com.uplog.uplog.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@OpenAPIDefinition(
        info = @Info(title = "테스트",
                description = "테스트",
                version = "v.1.0.0")) //현재 진행 중인 버전 명시 가능
@RequiredArgsConstructor
@Configuration
public class Swagger {

    //도메인 별 컨트롤러끼리 group을 묶든 url에 맞게, 용도에 맞게 그룹으로 묶으면 될듯

    //member
    @Bean
    public GroupedOpenApi memberOpenApi() {
        String[] paths = {"/member/**"};

        return GroupedOpenApi.builder()
                .group("member")
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
}
