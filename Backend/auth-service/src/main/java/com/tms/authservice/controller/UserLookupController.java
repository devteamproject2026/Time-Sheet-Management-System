package com.tms.authservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tms.authservice.dto.UserLookupResponse;
import com.tms.authservice.service.AuthService;

/**
 * HR-only lookup APIs used to populate Manager and Employee dropdowns.
 *
 * User accounts belong to Auth Service. Business Service accepts the selected
 * user ID but does not expose passwords or manage user accounts.
 */
@RestController
@RequestMapping("/api/auth/users")
@PreAuthorize("hasRole('HR_HEAD')")
public class UserLookupController {

    private final AuthService authService;

    public UserLookupController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * GET /api/auth/users/managers
     *
     * Returns approved and active Managers for the Project creation form.
     */
    @GetMapping("/managers")
    public ResponseEntity<List<UserLookupResponse>> getActiveManagers() {

        return ResponseEntity.ok(authService.getActiveManagers());
    }

    /**
     * GET /api/auth/users/employees
     *
     * Returns approved and active Employees for Project assignment forms.
     */
    @GetMapping("/employees")
    public ResponseEntity<List<UserLookupResponse>> getActiveEmployees() {

        return ResponseEntity.ok(authService.getActiveEmployees());
    }
}
