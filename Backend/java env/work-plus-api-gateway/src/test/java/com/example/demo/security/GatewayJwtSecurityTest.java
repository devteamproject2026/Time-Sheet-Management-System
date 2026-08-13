package com.example.demo.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

class GatewayJwtSecurityTest {

    private static final String SECRET =
            "VGhpc0lzQVN1cGVyU2VjcmV0S2V5Rm9ySldUU2lnbmluZ1RoYXRNdXN0QmVBdExlYXN0MzJCeXRlc0xvbmc=";

    private final GatewayJwtService jwtService = new GatewayJwtService(SECRET);
    private final JwtAuthenticationGlobalFilter filter =
            new JwtAuthenticationGlobalFilter(jwtService);

    @Test
    void validCookieAllowsProtectedRequestToContinue() {
        AtomicBoolean routed = new AtomicBoolean(false);
        MockServerWebExchange exchange = protectedRequest(validToken());

        filter.filter(exchange, ignoredExchange -> {
            routed.set(true);
            return Mono.empty();
        }).block();

        assertThat(routed).isTrue();
        assertThat(exchange.getResponse().getStatusCode()).isNull();
    }

    @Test
    void missingCookieReturnsUnauthorizedWithoutRouting() {
        AtomicBoolean routed = new AtomicBoolean(false);
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/business/projects").build());

        filter.filter(exchange, ignoredExchange -> {
            routed.set(true);
            return Mono.empty();
        }).block();

        assertThat(routed).isFalse();
        assertThat(exchange.getResponse().getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void modifiedCookieReturnsUnauthorizedWithoutRouting() {
        String token = validToken();
        String modifiedToken = token.substring(0, token.length() - 1)
                + (token.endsWith("a") ? "b" : "a");

        assertRejected(modifiedToken);
    }

    @Test
    void expiredCookieReturnsUnauthorizedWithoutRouting() {
        assertRejected(token(
                Instant.now().minusSeconds(120),
                Instant.now().minusSeconds(60),
                "EMPLOYEE"));
    }

    @Test
    void loginRemainsPublicWithoutCookie() {
        AtomicBoolean routed = new AtomicBoolean(false);
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/api/auth/login").build());

        filter.filter(exchange, ignoredExchange -> {
            routed.set(true);
            return Mono.empty();
        }).block();

        assertThat(routed).isTrue();
    }

    private void assertRejected(String token) {
        AtomicBoolean routed = new AtomicBoolean(false);
        MockServerWebExchange exchange = protectedRequest(token);

        filter.filter(exchange, ignoredExchange -> {
            routed.set(true);
            return Mono.empty();
        }).block();

        assertThat(routed).isFalse();
        assertThat(exchange.getResponse().getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private MockServerWebExchange protectedRequest(String token) {
        return MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/business/projects")
                        .cookie(new HttpCookie("jwt", token))
                        .build());
    }

    private String validToken() {
        return token(Instant.now(), Instant.now().plusSeconds(3600), "HR_HEAD");
    }

    private String token(Instant issuedAt, Instant expiresAt, String role) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
        return Jwts.builder()
                .subject("juned")
                .claim("role", role)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(key)
                .compact();
    }
}
