package com.base.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")    // 모든 엔드포인트에 대해 CORS 허용
                .allowedOriginPatterns("https://travel-pick.com", "https://www.travel-pick.com", "*://localhost:5173", "https://*travelpick.store")  // 허용할 클라이언트 도메인
                // .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true); // 자격 증명 허용 (쿠키, 인증 헤더 등)
    }
  
}
