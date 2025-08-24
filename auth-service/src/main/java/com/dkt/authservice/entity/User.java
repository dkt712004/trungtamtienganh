package com.dkt.authservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
        private Boolean isActive;

        // dùng JWT mặc định
//        Đây là phương thức bắt buộc khi implement interface UserDetails của Spring Security.
//        Nó trả về danh sách quyền (authorities/roles) mà user này có.
//        Trong code của bạn: luôn trả về duy nhất một quyền "ROLE_USER".
//                → Nghĩa là bất kỳ user nào trong DB khi đăng nhập thành công cũng sẽ có role "ROLE_USER" mặc định.
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        }

        @Override
        public String getPassword() {
                return "{noop}" + password; // chả đúng định dạng token
        }

        @Override
        public String getUsername() {
                return getEmail();
        }
}
