package com.dkt.authservice.service;

import com.dkt.authservice.dto.AuthenticationResult;
import com.dkt.authservice.dto.LoginRequest;
import com.dkt.authservice.entity.User;
import com.dkt.authservice.repository.UserRepository;
import com.dkt.authservice.security.jwt.JwtTokenProvider;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    public AuthService(AuthenticationManager am,
                       JwtTokenProvider jwt,
                       UserRepository userRepo,
                       MessageSource messageSource) {
        this.authenticationManager = am;
        this.jwtTokenProvider = jwt;
        this.userRepository = userRepo;
        this.messageSource = messageSource;
    }

    public AuthenticationResult login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Lấy lại thông tin User đầy đủ từ DB
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("error.user.notfound.after.auth", null, LocaleContextHolder.getLocale())
                ));

        // --- LOGIC MỚI ---
        // Lấy danh sách tên vai trò (List<String>) từ DB
        List<String> roles = userRepository.findRolesByEmail(user.getEmail());

        // Gọi đến JwtTokenProvider với đầy đủ thông tin
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), roles);
        // -----------------

        // Chuyển đổi List<String> sang Set<String> để trả về
        Set<String> rolesSet = roles.stream().collect(Collectors.toSet());
        return new AuthenticationResult(token, user, rolesSet);
    }
}