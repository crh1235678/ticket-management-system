package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // 🔥 放行预检请求（关键）
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 放行验证码接口
                        .requestMatchers("/vaeee/common/getCaptcha").permitAll()

                        // 其他接口（你可以先全放开）
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
