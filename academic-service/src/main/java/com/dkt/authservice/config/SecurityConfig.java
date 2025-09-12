package com.dkt.authservice.config;
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

    /**
     * Cấu hình bảo mật tối giản.
     * Vì Gateway đã xác thực và được tin tưởng, service này có thể cho phép
     * tất cả các request đã được chuyển tiếp đi qua.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Tắt CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // Bắt đầu định nghĩa quy tắc
                .authorizeHttpRequests(authorize -> authorize
                        // Cho phép TẤT CẢ các request đi qua mà không cần kiểm tra thêm
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}