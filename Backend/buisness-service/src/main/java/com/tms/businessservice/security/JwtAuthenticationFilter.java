package com.tms.businessservice.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Runs once before every Business Service request.
 *
 * It reads the "jwt" cookie, validates it, and tells Spring Security which user
 * and role are making the request.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String JWT_COOKIE_NAME = "jwt";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String token = findJwtCookie(request);

        // No cookie means the request remains unauthenticated. SecurityConfig
        // will return HTTP 401 when the requested endpoint requires login.
        if (token == null
                || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            JwtService.JwtUser jwtUser = jwtService.parseAndValidate(token);

            // Spring Security expects ROLE_ before role names used by hasRole().
            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority("ROLE_" + jwtUser.role());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            jwtUser.username(),
                            null,
                            List.of(authority));

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException | IllegalArgumentException exception) {
            // Invalid, modified, or expired JWTs are treated like no login.
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String findJwtCookie(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (JWT_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
