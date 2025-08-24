package com.dkt.authservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration configuration;

    // Bean xử lý quá trình xác thực.
    @Bean
    public AuthenticationManager authenticationManager(
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Bean cấu hình chuỗi bộ lọc bảo mật của Spring.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Tắt CSRF vì dùng API stateless
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                // Yêu cầu Spring Security không tạo và quản lý session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Bắt đầu định nghĩa các quy tắc truy cập
                .authorizeHttpRequests(authorize -> authorize
                        // Cho phép tất cả các request đi vào đường dẫn /api/auth/** mà không cần xác thực
                        .requestMatchers("/api/auth/**").permitAll()
                        // Mọi request khác đều phải được xác thực
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}