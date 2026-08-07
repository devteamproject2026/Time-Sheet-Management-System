package com.tms.transactionservice.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tms.transactionservice.dto.response.AttendanceResponse;
import com.tms.transactionservice.service.AttendanceService;

@RestController
@RequestMapping("/api/transactions/attendance")
public class AttendanceController {

    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @PostMapping("/check-in")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public AttendanceResponse checkIn(Authentication authentication) {
        return service.checkIn(authentication.getName());
    }

    @PutMapping("/check-out")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public AttendanceResponse checkOut(Authentication authentication) {
        return service.checkOut(authentication.getName());
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<AttendanceResponse> myAttendance(Authentication authentication) {
        return service.myAttendance(authentication.getName());
    }

    @GetMapping("/team")
    @PreAuthorize("hasRole('MANAGER')")
    public List<AttendanceResponse> teamAttendance(
            Authentication authentication,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {
        return service.teamAttendance(authentication.getName(), date);
    }
}
