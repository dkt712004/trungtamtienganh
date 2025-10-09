package com.dkt.authservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@Data
public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "email")
        private String email;

        @Column(name = "password")
        private String password;

        @Column(name = "full_name")
        private String fullName;

        @Column(name = "is_active")
        private Boolean isActive;

        public boolean isActive() {
                return this.isActive;
        }

}
