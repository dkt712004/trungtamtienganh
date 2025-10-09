package com.dkt.gatewayservice.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.List;

public class AuthenticationGatewayFilterFactory
        extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationGatewayFilterFactory.class);

    private final String jwtSecret;

    private final List<String> openApiEndpoints = List.of(
            "/api/auth/login",
            "/api/users/register"
    );

    public AuthenticationGatewayFilterFactory(String jwtSecret) {
        super(Config.class);
        this.jwtSecret = jwtSecret;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            if (isEndpointPublic(path)) {
                return chain.filter(exchange);
            }

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                logger.warn("Missing Authorization header for protected path: {}", path);
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Invalid Authorization header format for path: {}", path);
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
            String token = authHeader.substring(7);

            try {
                // Bước 1: Giải mã và xác thực token để lấy toàn bộ claims
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                // Bước 2: Trích xuất TẤT CẢ thông tin cần thiết
                String userEmail = claims.getSubject();
                List<String> rolesList = claims.get("roles", List.class);
                Integer userIdInt = claims.get("userId", Integer.class); // <-- LẤY USER ID

                // Chuyển đổi List<String> thành một chuỗi duy nhất
                String userRoles = rolesList != null ? String.join(",", rolesList) : "";

                if (userIdInt == null) {
                    throw new RuntimeException("User ID not found in token");
                }
                String userId = userIdInt.toString();

                // Bước 3: "Làm giàu" request bằng cách thêm các custom header
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Email", userEmail)
                        .header("X-User-Roles", userRoles)
                        .header("X-User-Id", userId)
                        .build();

                ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

                logger.info("Authenticated user: {}, id: {}, roles: {}. Forwarding to path: {}", userEmail, userId, userRoles, path);

                // Bước 4: Cho phép request đã được làm giàu đi tiếp
                return chain.filter(mutatedExchange);

            } catch (Exception e) {
                logger.error("Invalid JWT token for path: {}. Error: {}", path, e.getMessage());
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private boolean isEndpointPublic(String path) {
        return openApiEndpoints.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public static class Config {}
}