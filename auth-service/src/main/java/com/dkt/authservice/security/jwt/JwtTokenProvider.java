package com.dkt.authservice.security.jwt;

// --- CÁC IMPORT CHÍNH XÁC ---
import com.dkt.authservice.entity.Role;
import com.dkt.authservice.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key; // <-- Phải là từ java.security
import java.util.Date;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;
// ----------------------------

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationInMs;

    /**
     * Tạo token từ các thông tin người dùng được cung cấp.
     * Hàm này không còn phụ thuộc vào cấu trúc của User Entity.
     * @param userId ID của người dùng.
     * @param email Email của người dùng (sẽ là subject của token).
     * @param roles Danh sách tên các vai trò (List<String>).
     * @return Chuỗi JWT.
     */
    public String generateToken(Long userId, String email, List<String> roles) {
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(email)
                .claim("roles", roles)
                .claim("userId", userId)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Giải mã token và trích xuất email (subject) từ payload.
     * @param token chuỗi JWT.
     * @return email của người dùng.
     */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Kiểm tra xem một chuỗi JWT có hợp lệ hay không.
     * Hợp lệ nghĩa là: có chữ ký đúng, chưa hết hạn, và không bị sai định dạng.
     * @param token chuỗi JWT cần kiểm tra.
     * @return true nếu token hợp lệ, false nếu không.
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Hàm private helper để giải mã và xác thực token, trả về payload (claims).
     * Tái sử dụng code cho cả getUsernameFromToken và validateToken.
     */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Tạo key bí mật dùng để ký và xác thực token.
     */
    private Key key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}