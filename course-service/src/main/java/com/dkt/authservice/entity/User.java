package com.dkt.authservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set; // Import Set
import java.util.stream.Collectors; // Import Collectors

@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {

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
        private boolean isActive; // Dùng kiểu boolean nguyên thủy

        // Thêm lại mối quan hệ với Role để có thể lấy vai trò thật
        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(name = "user_roles",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id"))
        private Set<Role> roles;

        // --- CÁC PHƯƠNG THỨC BẮT BUỘC CỦA UserDetails ---

        /**
         * NÂNG CẤP: Lấy vai trò thật từ database thay vì hardcode.
         */
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
                if (roles == null) {
                        return Collections.emptyList();
                }
                return roles.stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList());
        }

        /**
         * Trả về mật khẩu đã được mã hóa.
         * BỎ "{noop}" đi vì chúng ta đang dùng BCryptPasswordEncoder.
         */
        @Override
        public String getPassword() {
                return password;
        }

        /**
         * Trả về username (chính là email).
         */
        @Override
        public String getUsername() {
                return email;
        }

        /**
         * THÊM VÀO: Kiểm tra tài khoản có hết hạn không.
         * Luôn trả về true để đơn giản hóa.
         */
        @Override
        public boolean isAccountNonExpired() {
                return true;
        }

        /**
         * THÊM VÀO: Kiểm tra tài khoản có bị khóa không.
         * Luôn trả về true để đơn giản hóa.
         */
        @Override
        public boolean isAccountNonLocked() {
                return true;
        }

        /**
         * THÊM VÀO: Kiểm tra thông tin xác thực (mật khẩu) có hết hạn không.
         * Luôn trả về true để đơn giản hóa.
         */
        @Override
        public boolean isCredentialsNonExpired() {
                return true;
        }

        /**
         * THÊM VÀO: Kiểm tra tài khoản có được kích hoạt không.
         * Phương thức này sẽ trả về giá trị của trường 'isActive'.
         */
        @Override
        public boolean isEnabled() {
                return this.isActive;
        }
}