package com.tms.authservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomUserDetailsService userDetailsService,
            BCryptPasswordEncoder passwordEncoder,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    // =============================================
    // Authentication Provider
    // =============================================
    @Bean
    public AuthenticationProvider authenticationProvider() {

    	DaoAuthenticationProvider provider =
    	        new DaoAuthenticationProvider(userDetailsService);

    	provider.setPasswordEncoder(passwordEncoder);

    	return provider;
    }



    // =============================================
    // Security Filter Chain
    // =============================================
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    	http
        .csrf(csrf -> csrf.disable())

 
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // URL Authorization
                .authorizeHttpRequests(auth -> auth

                        // Public APIs
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register-hr",
                                "/api/auth/logout"
                        ).permitAll()

                        // Admin APIs
                        .requestMatchers(
                                "/api/auth/approve-hr/**",
                                "/api/auth/reject-hr/**"
                        ).hasRole("ADMIN")

                        // HR APIs
                        .requestMatchers(
                                "/api/auth/register-manager",
                                "/api/auth/register-employee",
                                "/api/auth/users/managers",
                                "/api/auth/users/employees"
                        ).hasRole("HR_HEAD")

                        // Manager APIs
                        .requestMatchers(
                                "/api/manager/**"
                        ).hasRole("MANAGER")

                        // Employee APIs
                        .requestMatchers(
                                "/api/employee/**"
                        ).hasRole("EMPLOYEE")

                        // Any authenticated user
                        .anyRequest().authenticated()
                )

                // Authentication Provider
                .authenticationProvider(authenticationProvider())

                // JWT Filter
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
