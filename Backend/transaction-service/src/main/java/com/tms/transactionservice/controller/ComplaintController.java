package com.tms.transactionservice.controller;

import java.util.List;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.tms.transactionservice.dto.CreateComplaintRequest;
import com.tms.transactionservice.dto.ResolveComplaintRequest;
import com.tms.transactionservice.entity.Complaint;
import com.tms.transactionservice.service.ComplaintService;

/** APIs for the complaints table: employee escalation and manager resolution. */
@RestController
@RequestMapping("/api/transactions/complaints")
public class ComplaintController {
    private final ComplaintService service;
    public ComplaintController(ComplaintService service) { 
    	this.service = service; 
    	}

    @PostMapping 
    @PreAuthorize("hasRole('EMPLOYEE')")
    ResponseEntity<Complaint> raise(Authentication authentication, @Valid @RequestBody CreateComplaintRequest request) {
    	
        return ResponseEntity.status(HttpStatus.CREATED)
        		.body(service.raiseComplaint(authentication.getName(), request));
    }

    @GetMapping("/my") 
    @PreAuthorize("hasRole('EMPLOYEE')")
    List<Complaint> myComplaints(Authentication authentication) {
    	
    	return service.myComplaints(authentication.getName()); 
    	
    }

    @GetMapping("/assigned")
    @PreAuthorize("hasRole('MANAGER')")
    List<Complaint> assignedComplaints(Authentication authentication) { 
    	
    	return service.assignedComplaints(authentication.getName()); 
    	
    }

    @PutMapping("/{complaintId}/resolve") 
    @PreAuthorize("hasRole('MANAGER')")
    Complaint resolve(Authentication authentication, @PathVariable Integer complaintId, @Valid @RequestBody ResolveComplaintRequest request) {
    	
        return service.resolveComplaint(authentication.getName(), complaintId, request);
        
    }
}
