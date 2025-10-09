package com.dkt.authservice.client;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String fullName;
    private boolean isActive;
    private LocalDateTime createdAt;
    private Set<String> roles;
}