package com.tms.authservice.security;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   CustomUserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Get Authorization header from request
        final String authHeader = request.getHeader("Authorization");

        // Variables to store JWT and username
        String jwt = null;
        String username = null;

        // Check if Authorization header is missing or invalid
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Remove "Bearer " prefix
        jwt = authHeader.substring(7);

        // Extract username from JWT
        username = jwtService.extractUsername(jwt);

        // Authenticate only if user is not already authenticated
        if (username != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user from database
            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            // Validate JWT
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // Create Authentication object
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                // Add request details
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                // Store authentication in Security Context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continue request
        filterChain.doFilter(request, response);
    }    
    
    

}