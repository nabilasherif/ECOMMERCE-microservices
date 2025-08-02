package main.microservices.wallet.middleware;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtService {

    private static Key secretKey;
    private static long expirationTime;

    @Value("${jwt.secret:#{systemEnvironment['JWT_SECRET_ECOMMERCE']}}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    @PostConstruct
    public void init() {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("JWT secret must be configured");
        }
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        expirationTime = expiration;
    }

    public static String generateToken(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
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

//    public static void validateUserOwnership(Long tokenUserId, Long targetUserId) {
//        if (!tokenUserId.equals(targetUserId)) {
//            throw new SecurityException("Unauthorized access");
//        }
//    }
}