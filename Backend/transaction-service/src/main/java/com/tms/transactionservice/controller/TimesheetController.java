package com.tms.transactionservice.controller;

import java.util.List;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.tms.transactionservice.dto.SubmitTimesheetRequest;
import com.tms.transactionservice.entity.Timesheet;
import com.tms.transactionservice.service.TimesheetService;

/** APIs for the timesheets table: employee submission, history, and team view. */
@RestController
@RequestMapping("/api/transactions/timesheets")
public class TimesheetController {
    private final TimesheetService service;
    
    public TimesheetController(TimesheetService service) { 
    	this.service = service; 
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    ResponseEntity<Timesheet> submit(Authentication authentication, @Valid @RequestBody SubmitTimesheetRequest request) {
       
    	return ResponseEntity.status(HttpStatus.CREATED).body(service.submitTimesheet(authentication.getName(), request));
    }

    @GetMapping("/my") 
    @PreAuthorize("hasRole('EMPLOYEE')")
    List<Timesheet> myHistory(Authentication authentication) { 
    	
    	return service.myTimesheets(authentication.getName()); 
    }

    @GetMapping("/review") 
    @PreAuthorize("hasRole('MANAGER')")
    List<Timesheet> teamTimesheets(Authentication authentication) { 
    	
    	return service.timesheetsForManager(authentication.getName()); 
    }
}
