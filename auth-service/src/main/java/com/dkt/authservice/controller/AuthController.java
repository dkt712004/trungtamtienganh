package com.dkt.authservice.controller;

import com.dkt.authservice.dto.AuthenticationResult;
import com.dkt.authservice.dto.LoginRequest;
import com.dkt.authservice.dto.LoginResponse;
import com.dkt.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication API", description = "Các API để xác thực và quản lý token")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Đăng nhập và nhận JWT token",
            description = "Cung cấp email và mật khẩu để nhận về access token và thông tin người dùng.")
    @ApiResponse(responseCode = "200", description = "Đăng nhập thành công",
            content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    @ApiResponse(responseCode = "401", description = "Thông tin đăng nhập không chính xác")

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // 1. Gọi service và nhận về đối tượng kết quả
        AuthenticationResult result = authService.login(loginRequest);

        // 2. Tạo LoginResponse từ đối tượng kết quả
        LoginResponse response = new LoginResponse(
                result.getToken(),
                result.getUser().getId(),
                result.getUser().getEmail(),
                result.getUser().getFullName(),
                result.getRoles()
        );

        // 3. Trả về cho client
        return ResponseEntity.ok(response);
    }
}