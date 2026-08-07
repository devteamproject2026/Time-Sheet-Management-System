package com.example.demo.security;

import java.util.Set;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/** Verifies tokens issued by Auth Service before a request is routed. */
@Service
public class GatewayJwtService {

    private static final Set<String> SUPPORTED_ROLES =
            Set.of("ADMIN", "HR_HEAD", "MANAGER", "EMPLOYEE");

    private final SecretKey signingKey;

    public GatewayJwtService(@Value("${jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    /** JJWT validates the digital signature and expiration while parsing. */
    public JwtUser parseAndValidate(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String username = claims.getSubject();
        String role = claims.get("role", String.class);

        if (username == null || username.isBlank()) {
            throw new JwtException("JWT does not contain a username");
        }
        if (role == null || !SUPPORTED_ROLES.contains(role)) {
            throw new JwtException("JWT contains an unsupported role");
        }

        return new JwtUser(username, role);
    }

    public record JwtUser(String username, String role) {
    }
}
