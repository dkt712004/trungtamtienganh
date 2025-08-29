package com.dkt.academicservice.security.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final Key key;
    private final long jwtExpirationInMs;

    /**
     * Constructor Injection: Spring sẽ tự động tìm các giá trị trong file properties
     * và "tiêm" chúng vào đây khi khởi tạo bean này.
     */
    public JwtTokenProvider(
            // SỬA LẠI TÊN THUỘC TÍNH Ở ĐÂY
            @Value("${app.jwt-secret}") String jwtSecret,
            // SỬA LẠI TÊN THUỘC TÍNH Ở ĐÂY
            @Value("${app.jwt-expiration-milliseconds}") long jwtExpirationInMs) {

        // Tạo key một lần duy nhất khi khởi tạo
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    /**
     * Tạo ra một JWT token mới.
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        // Lấy danh sách vai trò dưới dạng Set<String>
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("roles", roles)         // Thêm roles (dưới dạng một mảng JSON)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Lấy email (username) từ một token đã được xác thực.
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Kiểm tra xem token có hợp lệ không (chữ ký, thời gian hết hạn).
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            logger.error("JWT validation error: {}", ex.getMessage());
            return false;
        }
    }

}