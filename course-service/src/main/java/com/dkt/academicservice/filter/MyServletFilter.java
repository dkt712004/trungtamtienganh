package com.dkt.academicservice.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyServletFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    private static final String SECRET_KEY = "DayLaMotChuoiBiMatRatDaiVaAnToanDungDeKyTokenJWT";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.info("Request URI: {}", requestURI);

        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            log.info("Authorization header: '{}'", authorization);
            String jwt = authorization.substring(7).trim(); // bỏ khoảng trắng dư
            log.info("Raw JWT: '{}'", jwt);

            try {
                // Debug token
                String[] parts = jwt.split("\\.");
                log.info("Token parts length: {}", parts.length);
                log.info("Token parts: {}", Arrays.toString(parts));

                // Decode header + payload để xem rõ
                if (parts.length >= 2) {
                    String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
                    String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
                    log.info("Decoded Header: {}", headerJson);
                    log.info("Decoded Payload: {}", payloadJson);
                }

                // Debug secret key
                byte[] secretBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
                log.info("SECRET_KEY length: {}", SECRET_KEY.length());
                log.info("SECRET_KEY bytes length: {}", secretBytes.length);

                // Parse & verify token
                Key key = Keys.hmacShaKeyFor(secretBytes);
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                String username = claims.getSubject();
                Date expiration = claims.getExpiration();

                log.info("Token username: {}", username);
                log.info("Token expiration: {}", expiration);

                if (expiration.before(new Date())) {
                    log.warn("Token expired!");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                log.info("Token is valid, continue request...");

            } catch (Exception e) {
                log.error("Invalid token: {}", e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
