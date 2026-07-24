package com.tms.authservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.tms.authservice.dto.LoginRequest;
import com.tms.authservice.dto.LoginResponse;
import com.tms.authservice.dto.RegisterHrRequest;
import com.tms.authservice.entity.User;
import com.tms.authservice.entity.enums.AccountStatus;
import com.tms.authservice.entity.enums.ApprovalStatus;
import com.tms.authservice.entity.enums.Role;
import com.tms.authservice.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Validated
@CrossOrigin(origins = "http://localhost:5173")
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
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    //==============================================
    // Get Pending HR Requests
    //==============================================
    @GetMapping("/pending-hr")
    public ResponseEntity<List<User>> getPendingHrRequests() {

        return ResponseEntity.ok(authService.getPendingHrRequests());
    }

    //==============================================
    // Approve HR
    //==============================================
    @PutMapping("/approve-hr/{id}")
    public ResponseEntity<String> approveHr(
            @PathVariable Integer id) {

        return ResponseEntity.ok(authService.approveHr(id));
    }

    //==============================================
    // Reject HR
    //==============================================
    @PutMapping("/reject-hr/{id}")
    public ResponseEntity<String> rejectHr(
            @PathVariable Integer id) {

        return ResponseEntity.ok(authService.rejectHr(id));
    }
}