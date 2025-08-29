package com.dkt.academicservice.security;

import com.dkt.academicservice.entity.User;
import com.dkt.academicservice.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String email = authentication.getName();
        String rawPassword = authentication.getCredentials().toString();

        // 1. Check user tồn tại
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Email not found"));



        // 2. Compare password
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        // 3. Load roles từ repository
        List<String> roles = userRepository.findRolesByEmail(email);

        return new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getPassword(),
                roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
