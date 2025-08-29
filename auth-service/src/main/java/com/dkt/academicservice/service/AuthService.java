package com.dkt.academicservice.service;

import com.dkt.academicservice.dto.AuthenticationResult;
import com.dkt.academicservice.dto.LoginRequest;
import com.dkt.academicservice.entity.User;
import com.dkt.academicservice.repository.UserRepository;
import com.dkt.academicservice.security.jwt.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository; // <-- Thêm UserRepository

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       UserRepository userRepository) { // <-- Sửa constructor
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    // Thay đổi kiểu trả về từ String sang AuthenticationResult
    public AuthenticationResult login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 1. Tạo token
        String token = jwtTokenProvider.generateToken(authentication);

        // 2. Lấy lại thông tin User đầy đủ từ database
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Lỗi không mong muốn: Không tìm thấy user sau khi xác thực."));

        // 3. Lấy danh sách vai trò
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // 4. Trả về đối tượng kết quả chứa tất cả thông tin
        return new AuthenticationResult(token, user, roles);
    }
}