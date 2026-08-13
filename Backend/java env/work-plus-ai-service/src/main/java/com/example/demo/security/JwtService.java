package com.example.demo.security;

import java.util.Base64;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/** Validates tokens issued by Auth Service; it never creates a token. */
@Service
public class JwtService {
    private final SecretKey signingKey;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    public JwtUser parseAndValidate(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return new JwtUser(claims.getSubject(), claims.get("role", String.class));
    }

    public record JwtUser(String username, String role) { }
}
