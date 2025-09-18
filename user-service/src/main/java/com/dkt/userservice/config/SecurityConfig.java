package com.dkt.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * Vẫn cần PasswordEncoder để mã hóa mật khẩu khi đăng ký và đổi mật khẩu.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // Gateway da duoc xac thuc nen service nay co th cho phep tat ca cac request da duoc chuyen tiep di qua
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Tắt CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // Bắt đầu định nghĩa quy tắc
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // Cho phep tat ca cac request di qua ma khong can kiem tra
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}