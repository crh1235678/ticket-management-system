package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有本地域名进行跨域调用，因为前端给的端口可能会发生变化
        config.addAllowedOriginPattern("http://localhost:*");
        //上线时不能写 *，要写死域名
        //config.addAllowedOrigin("https://你的前端域名.com");
        config.addAllowedHeader("*"); // 允许所有请求头
        config.addAllowedMethod("*"); // GET, POST, PUT, DELETE, OPTIONS
        config.setAllowCredentials(true); // 允许携带 cookie / token

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}