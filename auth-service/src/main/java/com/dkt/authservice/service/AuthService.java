package com.dkt.authservice.service;

import com.dkt.authservice.dto.LoginRequest;
import com.dkt.authservice.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;


    public String login(LoginRequest loginRequest) {
        System.out.println("authenticationManager class: " + authenticationManager.getClass());
        System.out.println("authenticationManager interfaces: " + Arrays.toString(authenticationManager.getClass().getInterfaces()));

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()));

            System.out.println("Authentication object: " + authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return jwtTokenProvider.generateToken(authentication);

        } catch (Throwable e) {
            System.out.println(e);
            return null;
        }


    }
}
