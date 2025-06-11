package com.grepp.diary.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /uploads/** 경로로 들어오는 요청을 프로젝트 폴더의 /photo/ 디렉토리에서 찾도록 설정
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./photo/"); // 프로젝트 루트 폴더 기준 photo/ 디렉토리
    }
}
