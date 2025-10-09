package com.dkt.authservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class LoginResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String email;
    private String fullName;
    private Set<String> roles; // Dùng Set để đảm bảo các vai trò không bị trùng lặp

    public LoginResponse(String accessToken, Long userId, String email, String fullName, Set<String> roles) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
    }
}