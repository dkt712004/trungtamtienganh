package com.dkt.userservice.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String fullName;
    private boolean isActive;
    private LocalDateTime createdAt;
    private Set<String> roles; // Chỉ trả về tên của vai trò, không phải toàn bộ đối tượng Role
}