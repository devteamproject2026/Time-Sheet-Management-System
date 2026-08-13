package com.tms.transactionservice.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tms.transactionservice.dto.response.EmployeeReportResponse;
import com.tms.transactionservice.service.EmployeeReportService;

@RestController
@RequestMapping("/api/transactions/reports/employees")
@PreAuthorize("hasRole('MANAGER')")
public class EmployeeReportController {

    private final EmployeeReportService service;

    public EmployeeReportController(EmployeeReportService service) {
        this.service = service;
    }

    @GetMapping
    public List<EmployeeReportResponse> all(Authentication authentication) {
        return service.myEmployeeReports(authentication.getName());
    }

    @GetMapping("/{employeeId}")
    public EmployeeReportResponse one(
            Authentication authentication,
            @PathVariable Integer employeeId) {
        return service.myEmployeeReport(authentication.getName(), employeeId);
    }
}
