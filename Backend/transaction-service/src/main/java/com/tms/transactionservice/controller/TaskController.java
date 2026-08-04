package com.tms.transactionservice.controller;

import java.util.List;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.tms.transactionservice.dto.CreateTaskRequest;
import com.tms.transactionservice.dto.UpdateTaskProgressRequest;
import com.tms.transactionservice.entity.Task;
import com.tms.transactionservice.service.TaskService;

/** APIs for the tasks table: assignment, acceptance, and employee progress. */
@RestController
@RequestMapping("/api/transactions/tasks")
public class TaskController {
    private final TaskService service;
    public TaskController(TaskService service) { this.service = service; }

    @PostMapping 
    @PreAuthorize("hasRole('MANAGER')")
    ResponseEntity<Task> create(Authentication authentication, @Valid @RequestBody CreateTaskRequest request) {
    	
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createTask(authentication.getName(), request));
        
    }

    @GetMapping("/my") 
    @PreAuthorize("hasRole('EMPLOYEE')")
    List<Task> myTasks(Authentication authentication) { 
    	
    	return service.myTasks(authentication.getName()); 
    	
    }

    @GetMapping("/managed") 
    @PreAuthorize("hasRole('MANAGER')")
    List<Task> managedTasks(Authentication authentication) { 
    	
    	return service.myManagedTasks(authentication.getName()); 
    }

    @PutMapping("/{taskId}/accept") 
    @PreAuthorize("hasRole('EMPLOYEE')")
    Task accept(Authentication authentication, @PathVariable Integer taskId) { 
    	
    	return service.acceptTask(authentication.getName(), taskId); 
    	}

    @PutMapping("/{taskId}/progress") 
    @PreAuthorize("hasRole('EMPLOYEE')")
    Task updateProgress(Authentication authentication, @PathVariable Integer taskId, @Valid @RequestBody UpdateTaskProgressRequest request) {
       
    	return service.updateProgress(authentication.getName(), taskId, request);
    }
}
