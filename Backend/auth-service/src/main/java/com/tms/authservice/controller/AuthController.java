package com.tms.authservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.tms.authservice.dto.CurrentUserResponse;
import com.tms.authservice.dto.LoginRequest;
import com.tms.authservice.dto.LoginResponse;
import com.tms.authservice.dto.RegisterHrRequest;
import com.tms.authservice.entity.User;
import com.tms.authservice.entity.enums.AccountStatus;
import com.tms.authservice.entity.enums.ApprovalStatus;
import com.tms.authservice.entity.enums.Role;
import com.tms.authservice.service.AuthService;

import jakarta.validation.Valid;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;



@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    //==============================================
    // Register HR
    //==============================================
    @PostMapping("/register-hr")
    public ResponseEntity<String> registerHr(
            @Valid @RequestBody RegisterHrRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registerUser(
                        request,
                        Role.HR_HEAD,
                        ApprovalStatus.PENDING,
                        AccountStatus.INACTIVE));
    }

    //==============================================
    // Register Manager
    //==============================================
    @PreAuthorize("hasRole('HR_HEAD')")
    @PostMapping("/register-manager")
    public ResponseEntity<String> registerManager(
            @Valid @RequestBody RegisterHrRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registerUser(
                        request,
                        Role.MANAGER,
                        ApprovalStatus.APPROVED,
                        AccountStatus.ACTIVE));
    }

    //==============================================
    // Register Employee
    //==============================================
    @PreAuthorize("hasRole('HR_HEAD')")
    @PostMapping("/register-employee")
    public ResponseEntity<String> registerEmployee(
            @Valid @RequestBody RegisterHrRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registerUser(
                        request,
                        Role.EMPLOYEE,
                        ApprovalStatus.APPROVED,
                        AccountStatus.ACTIVE));
    }

    //==============================================
    // Login
    //==============================================
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        LoginResponse loginResponse = authService.login(request);

        Cookie cookie = new Cookie("jwt", loginResponse.getToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(false);          // true in production
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);

        response.addCookie(cookie);

        //loginResponse.setToken(null);

        return ResponseEntity.ok(loginResponse);
    }

    //==============================================
    // Restore Current Login After Browser Refresh
    //==============================================
    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> getCurrentUser(
            Authentication authentication) {

        // JwtAuthenticationFilter validates the HttpOnly cookie before this
        // method runs, so authentication.getName() is the verified username.
        return ResponseEntity.ok(
                authService.getCurrentUser(authentication.getName()));
    }

    //==============================================
    // Get Pending HR Requests
    //==============================================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending-hr")
    public ResponseEntity<List<User>> getPendingHrRequests() {

        return ResponseEntity.ok(authService.getPendingHrRequests());
    }

    //==============================================
    // Approve HR
    //==============================================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/approve-hr/{id}")
    public ResponseEntity<String> approveHr(
            @PathVariable Integer id) {

        return ResponseEntity.ok(authService.approveHr(id));
    }

    //==============================================
    // Reject HR
    //==============================================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/reject-hr/{id}")
    public ResponseEntity<String> rejectHr(
            @PathVariable Integer id) {

        return ResponseEntity.ok(authService.rejectHr(id));
    }
    
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return ResponseEntity.ok("Logout Successful");
    }
    
    
    
    
}
