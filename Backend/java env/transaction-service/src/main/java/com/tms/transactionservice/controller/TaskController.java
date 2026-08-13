package com.tms.transactionservice.controller;

import java.util.List;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.tms.transactionservice.dto.CreateTaskRequest;
import com.tms.transactionservice.dto.UpdateTaskRequest;
import com.tms.transactionservice.dto.UpdateTaskProgressRequest;
import com.tms.transactionservice.dto.response.TaskResponse;
import com.tms.transactionservice.service.TaskService;

/** APIs for the tasks table: assignment, acceptance, and employee progress. */
@RestController
@RequestMapping("/api/transactions/tasks")
public class TaskController {
    private final TaskService service;
    public TaskController(TaskService service) { this.service = service; }

    @PostMapping 
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<TaskResponse> create(Authentication authentication, @Valid @RequestBody CreateTaskRequest request) {
    	
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createTask(authentication.getName(), request));
        
    }

    @GetMapping("/my") 
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<TaskResponse> myTasks(Authentication authentication) {
    	
    	return service.myTasks(authentication.getName()); 
    	
    }

    @GetMapping("/managed") 
    @PreAuthorize("hasRole('MANAGER')")
    public List<TaskResponse> managedTasks(Authentication authentication) {
    	
    	return service.myManagedTasks(authentication.getName()); 
    }

    @PutMapping("/{taskId}/accept") 
    @PreAuthorize("hasRole('EMPLOYEE')")
    public TaskResponse accept(Authentication authentication, @PathVariable Integer taskId) {
    	
    	return service.acceptTask(authentication.getName(), taskId); 
    	}

    @PutMapping("/{taskId}/progress") 
    @PreAuthorize("hasRole('EMPLOYEE')")
    public TaskResponse updateProgress(Authentication authentication, @PathVariable Integer taskId, @Valid @RequestBody UpdateTaskProgressRequest request) {
       
    	return service.updateProgress(authentication.getName(), taskId, request);
    }

    /** Owning Managers may update editable details while a Task is unfinished. */
    @PutMapping("/{taskId}")
    @PreAuthorize("hasRole('MANAGER')")
    public TaskResponse update(
            Authentication authentication,
            @PathVariable Integer taskId,
            @Valid @RequestBody UpdateTaskRequest request) {
        return service.updateTask(authentication.getName(), taskId, request);
    }
}
