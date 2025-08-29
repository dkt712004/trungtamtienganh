package com.dkt.userservice.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    private Key key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Hàm này không thay đổi
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * HÀM MỚI: Giải mã và trả về toàn bộ payload (claims) của token.
     * @param token Chuỗi JWT.
     * @return một đối tượng Claims chứa tất cả thông tin trong token.
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}