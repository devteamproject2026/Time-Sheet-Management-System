package com.tms.transactionservice.security;

import java.util.Base64;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/** Reads the JWT issued by Auth Service; this service does not issue tokens. */
@Service
public class JwtService {
    private final SecretKey signingKey;
    public JwtService(@Value("${jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }
    public JwtUser parseAndValidate(String token) {
        Claims claims = Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
        return new JwtUser(claims.getSubject(), claims.get("role", String.class));
    }
    public record JwtUser(String username, String role) {}
}
