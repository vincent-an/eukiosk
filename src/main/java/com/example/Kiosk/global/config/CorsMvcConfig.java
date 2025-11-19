package com.example.Kiosk.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        //controller에서 cors문제 처리
        corsRegistry.addMapping("/**")  // 모든 경로에 대해 CORS 설정
                .allowedOriginPatterns("http://localhost:3000", "http://127.0.0.1:3000",
                        "https://euljigraduation.netlify.app/", "http://10.205.113.235:3000/",
                        "http://192.168.0.17:3000");
        //"https://forchuncookie.netlify.app" 프론트 주소 추가
    }
}
