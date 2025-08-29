package com.dkt.academicservice.controller;

import com.dkt.academicservice.dto.AuthenticationResult;
import com.dkt.academicservice.dto.LoginRequest;
import com.dkt.academicservice.dto.LoginResponse;
import com.dkt.academicservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

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