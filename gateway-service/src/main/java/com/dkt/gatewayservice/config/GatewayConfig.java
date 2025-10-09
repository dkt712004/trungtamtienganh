package com.dkt.gatewayservice.config;

import com.dkt.gatewayservice.filter.AuthenticationGatewayFilterFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    /**
     * Định nghĩa một Bean cho AuthenticationGatewayFilterFactory.
     * Spring sẽ tự động tìm giá trị của 'app.jwt-secret' từ file properties
     * và truyền nó vào constructor của factory.
     * @param jwtSecret giá trị được inject từ file properties.
     * @return một instance của nhà máy tạo filter.
     */
    @Bean
    public AuthenticationGatewayFilterFactory authenticationGatewayFilterFactory(@Value("${app.jwt-secret}") String jwtSecret) {
        return new AuthenticationGatewayFilterFactory(jwtSecret);
    }
}