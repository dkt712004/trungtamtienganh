package com.dkt.authservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Authentication Service API",
        version = "1.0",
        description = "Tài liệu API cho Dịch vụ Xác thực và Cấp phát Token"
))
public class OpenApiConfig {
}