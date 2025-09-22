package net.mycompany.commerce.purchase.infrastructure.config.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        // HMAC key
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String clientId) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(clientId)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    public String validateToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}