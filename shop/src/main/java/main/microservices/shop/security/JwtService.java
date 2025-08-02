package main.microservices.shop.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtService {

    private static Key secretKey;

    @Value("${jwt.secret:#{systemEnvironment['JWT_SECRET_ECOMMERCE']}}")
    private String secret;

    @PostConstruct
    public void init() {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("JWT secret must be configured");
        }
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public static Long extractUserId(String token) throws SecurityException {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new SecurityException("Token is null or empty");
            }
            String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();

            Object userId = claims.get("userId");
            if (userId == null) {
                throw new SecurityException("User ID claim not found in token");
            }

            if (userId instanceof Long) {
                return (Long) userId;
            } else if (userId instanceof Integer) {
                return ((Integer) userId).longValue();
            } else {
                throw new SecurityException("User ID is not a valid number type");
            }

        } catch (Exception e) {
            throw new SecurityException("Invalid token: " + e.getMessage());
        }
    }

}
