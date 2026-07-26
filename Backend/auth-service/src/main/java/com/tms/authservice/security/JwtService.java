package com.tms.authservice.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.tms.authservice.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	//==========================================
    // Secret key from application.properties
	//==========================================
    @Value("${jwt.secret}")
    private String secretKey;

    //===========================================
    // JWT expiration time (in milliseconds)
    //===========================================
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    //============================================
    // Convert Base64 secret into signing key
    //============================================
    private Key getSigningKey() {

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    //=============================================
    // Generate JWT token for logged-in user
    //=============================================
    public String generateToken(User user) {

        // Store custom information inside JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());

        return Jwts.builder()

                // Custom claims
                .claims(claims)

                // Username of logged-in user
                .subject(user.getUsername())

                // Token creation time
                .issuedAt(new Date(System.currentTimeMillis()))

                // Token expiry time
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))

                // Digitally sign token using secret key
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)

                // Convert JWT into String
                .compact();
    }

    //===============================================
    // Extract all claims (payload) from JWT
    //===============================================
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //=============================================
    // Extract username (subject) from JWT
    //=============================================
    public String extractUsername(String token) {

        return extractAllClaims(token).getSubject();
    }
    

    //==============================================
    // Extract token expiration date
    //==============================================
    private Date extractExpiration(String token) {

        return extractAllClaims(token).getExpiration();
    }

    //=================================================
    // Check whether JWT is expired
    //=================================================
    public boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());
    }

    //===============================================
    // Validate JWT
    //===============================================
    public boolean isTokenValid(String token, UserDetails userDetails) {

        // Username stored inside JWT
        final String username = extractUsername(token);

        // Username must match and token must not be expired
        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

}