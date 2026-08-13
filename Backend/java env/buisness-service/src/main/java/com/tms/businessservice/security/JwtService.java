package com.tms.businessservice.security;

import java.util.Set;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Verifies and reads the JWT that Auth Service creates after login.
 *
 * Business Service does not check the user's password again. A valid digital
 * signature proves that the token was created by our trusted Auth Service.
 */
@Service
public class JwtService {

    private static final Set<String> SUPPORTED_ROLES =
            Set.of("ADMIN", "HR_HEAD", "MANAGER", "EMPLOYEE");

    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Checks the token signature and expiry, then returns the identity stored
     * inside it. JJWT automatically rejects expired or modified tokens.
     */
    public JwtUser parseAndValidate(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
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

    private SecretKey getSigningKey() {

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Small read-only object containing the authenticated user's JWT details.
     */
    public record JwtUser(String username, String role) {
    }
}
