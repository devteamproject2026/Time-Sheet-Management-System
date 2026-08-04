package com.tms.transactionservice.security;

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

/** Converts the existing HttpOnly jwt cookie into Spring's authenticated user. */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    public JwtAuthenticationFilter(JwtService jwtService) { this.jwtService = jwtService; }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = null;
        if (request.getCookies() != null) for (Cookie cookie : request.getCookies())
            if ("jwt".equals(cookie.getName())) { token = cookie.getValue(); break; }
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) try {
            JwtService.JwtUser user = jwtService.parseAndValidate(token);
            var auth = new UsernamePasswordAuthenticationToken(user.username(), null,
                List.of(new SimpleGrantedAuthority("ROLE_" + user.role())));
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (JwtException | IllegalArgumentException exception) { SecurityContextHolder.clearContext(); }
        chain.doFilter(request, response);
    }
}
