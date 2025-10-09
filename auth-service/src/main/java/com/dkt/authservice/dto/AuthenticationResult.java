package com.dkt.authservice.dto;

import com.dkt.authservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Set;

@Getter
@AllArgsConstructor // Tạo constructor nhận tất cả các trường
public class AuthenticationResult {
    private String token;
    private User user;
    private Set<String> roles;
}