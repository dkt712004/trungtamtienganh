package com.dkt.academicservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_roles")
@Data
@NoArgsConstructor
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "role_id")
    private Integer roleId;

    public UserRole(Long userId, Integer roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }
}