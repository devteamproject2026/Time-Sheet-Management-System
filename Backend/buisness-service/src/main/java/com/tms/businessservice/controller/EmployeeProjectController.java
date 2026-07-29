package com.tms.businessservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tms.businessservice.dto.request.EmployeeProjectRequest;
import com.tms.businessservice.dto.response.EmployeeProjectResponse;
import com.tms.businessservice.service.EmployeeProjectService;

import jakarta.validation.Valid;

/**
 * REST APIs for assigning Employees to Projects.
 *
 * Base URL: /api/business/employee-projects
 *
 * @PreAuthorize performs the first role check. The service performs a second,
 * data-level check so a Manager can operate only on their own Projects.
 */
@RestController
@RequestMapping("/api/business/employee-projects")
public class EmployeeProjectController {

    private final EmployeeProjectService employeeProjectService;

    public EmployeeProjectController(
            EmployeeProjectService employeeProjectService) {

        this.employeeProjectService = employeeProjectService;
    }

    /**
     * POST /api/business/employee-projects
     *
     * Assigns one active Employee to one Project.
     *
     * ADMIN/HR_HEAD may assign Employees to any Project.
     * MANAGER may assign Employees only to a Project managed by that Manager.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_HEAD', 'MANAGER')")
    @PostMapping
    public ResponseEntity<EmployeeProjectResponse> assignEmployee(
            @Valid @RequestBody EmployeeProjectRequest request,
            Authentication authentication) {

        EmployeeProjectResponse createdAssignment =
                employeeProjectService.assignEmployee(
                        request,
                        authentication.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdAssignment);
    }

    /**
     * GET /api/business/employee-projects
     *
     * Returns every Employee-Project assignment in the company.
     *
     * Allowed roles: ADMIN and HR_HEAD because this exposes assignments from
     * every Project and Manager.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_HEAD')")
    @GetMapping
    public ResponseEntity<List<EmployeeProjectResponse>> getAllAssignments(
            Authentication authentication) {

        return ResponseEntity.ok(
                employeeProjectService.getAllAssignments(
                        authentication.getName()));
    }

    /**
     * GET /api/business/employee-projects/project/{projectId}
     *
     * Returns the team assigned to one Project.
     *
     * ADMIN/HR_HEAD may view any Project team.
     * MANAGER may view only the team of a Project managed by that Manager.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_HEAD', 'MANAGER')")
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<EmployeeProjectResponse>>
            getAssignmentsByProject(
                    @PathVariable Integer projectId,
                    Authentication authentication) {

        return ResponseEntity.ok(
                employeeProjectService.getAssignmentsByProject(
                        projectId,
                        authentication.getName()));
    }

    /**
     * GET /api/business/employee-projects/employee/{employeeId}
     *
     * Returns all Project assignments for one Employee.
     *
     * Allowed roles: ADMIN and HR_HEAD. A Manager is not given this endpoint
     * because it could reveal the Employee's work under other Managers.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_HEAD')")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeProjectResponse>>
            getAssignmentsByEmployee(
                    @PathVariable Integer employeeId,
                    Authentication authentication) {

        return ResponseEntity.ok(
                employeeProjectService.getAssignmentsByEmployee(
                        employeeId,
                        authentication.getName()));
    }

    /**
     * GET /api/business/employee-projects/my-projects
     *
     * Lets the logged-in Employee view only their own Project assignments.
     * The Employee ID comes from the verified JWT username, not from a request
     * parameter, so an Employee cannot request another Employee's assignments.
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my-projects")
    public ResponseEntity<List<EmployeeProjectResponse>> getMyAssignments(
            Authentication authentication) {

        return ResponseEntity.ok(
                employeeProjectService.getMyAssignments(
                        authentication.getName()));
    }

    /**
     * DELETE /api/business/employee-projects/{employeeProjectId}
     *
     * Removes an Employee from a Project and returns HTTP 204 No Content.
     *
     * ADMIN/HR_HEAD may remove any assignment.
     * MANAGER may remove assignments only from their own Projects.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_HEAD', 'MANAGER')")
    @DeleteMapping("/{employeeProjectId}")
    public ResponseEntity<Void> removeAssignment(
            @PathVariable Integer employeeProjectId,
            Authentication authentication) {

        employeeProjectService.removeAssignment(
                employeeProjectId,
                authentication.getName());

        return ResponseEntity.noContent().build();
    }
}
