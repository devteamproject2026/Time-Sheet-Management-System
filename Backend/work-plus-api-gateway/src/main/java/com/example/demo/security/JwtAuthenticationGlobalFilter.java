package com.example.demo.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Set;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.JwtException;
import reactor.core.publisher.Mono;

/**
 * Rejects missing, modified, or expired JWT cookies before service discovery
 * and routing. Services verify the token again and enforce role permissions.
 */
@Component
public class JwtAuthenticationGlobalFilter implements GlobalFilter, Ordered {

    private static final String JWT_COOKIE_NAME = "jwt";
    private static final Set<String> PUBLIC_POST_PATHS = Set.of(
            "/api/auth/login",
            "/api/auth/register-hr",
            "/api/auth/logout");

    private final GatewayJwtService jwtService;

    public JwtAuthenticationGlobalFilter(GatewayJwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        // CORS preflight is handled by GatewayCorsConfig. Non-API paths are
        // allowed to continue so unmatched routes retain the normal 404.
        if (method == HttpMethod.OPTIONS
                || !path.startsWith("/api/")
                || (method == HttpMethod.POST && PUBLIC_POST_PATHS.contains(path))) {
            return chain.filter(exchange);
        }

        HttpCookie cookie = exchange.getRequest()
                .getCookies()
                .getFirst(JWT_COOKIE_NAME);

        if (cookie == null || cookie.getValue().isBlank()) {
            return unauthorized(exchange);
        }

        try {
            jwtService.parseAndValidate(cookie.getValue());
            return chain.filter(exchange);
        } catch (JwtException | IllegalArgumentException exception) {
            return unauthorized(exchange);
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = "{\"timestamp\":\"" + Instant.now()
                + "\",\"status\":401,\"error\":\"Unauthorized\","
                + "\"message\":\"Authentication is required\","
                + "\"validationErrors\":{}}";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    /** Run before Gateway resolves and forwards the route. */
    @Override
    public int getOrder() {
        return -100;
    }
}
