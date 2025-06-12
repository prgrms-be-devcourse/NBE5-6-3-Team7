package com.grepp.diary.infra.feign.config;

import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class FeignCommonConfig {

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
            5, TimeUnit.SECONDS,
            10, TimeUnit.SECONDS,
            true
        );
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                log.info("=========================================================");
                log.info("requestTemplate : {}", requestTemplate);
            }
        };
    }
}
