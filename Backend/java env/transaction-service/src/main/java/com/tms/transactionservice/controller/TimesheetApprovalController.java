package com.tms.transactionservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.tms.transactionservice.dto.ReviewTimesheetRequest;
import com.tms.transactionservice.dto.response.TimesheetResponse;
import com.tms.transactionservice.service.TimesheetApprovalService;

/** API for timesheet_approvals: a manager review creates an audit record. */
@RestController
@RequestMapping("/api/transactions/timesheet-approvals")
public class TimesheetApprovalController {
	
    private final TimesheetApprovalService service;
    
    public TimesheetApprovalController(TimesheetApprovalService service) {
    	
    	this.service = service; 
    	}

    @PostMapping("/timesheet/{timesheetId}") 
    @PreAuthorize("hasRole('MANAGER')")
    public TimesheetResponse review(Authentication authentication, @PathVariable Integer timesheetId, @Valid @RequestBody ReviewTimesheetRequest request) {
       
    	return service.reviewTimesheet(authentication.getName(), timesheetId, request);
    }
}
