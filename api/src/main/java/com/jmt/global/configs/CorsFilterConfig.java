package com.jmt.global.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsFilterConfig {

    @Value("${cors.allow.origins}")
    private String allowedOrigins;

    // Cors 관련 헤더 - 응답 헤더 추가
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*"); // 모든 요청 메서드 허용
        config.addAllowedHeader("*"); // 모든 HEADER 허용
        config.addAllowedOrigin(allowedOrigins); // 모든 URL 허용

        return new CorsFilter(source);
    }
}
