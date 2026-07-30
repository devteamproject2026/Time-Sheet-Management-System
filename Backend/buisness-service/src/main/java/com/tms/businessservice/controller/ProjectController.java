package com.tms.businessservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tms.businessservice.dto.request.ProjectRequest;
import com.tms.businessservice.dto.response.ProjectResponse;
import com.tms.businessservice.service.ProjectService;

import jakarta.validation.Valid;

/**
 * REST APIs for managing Projects.
 *
 * Base URL: /api/business/projects
 *
 * ADMIN and HR_HEAD manage the complete Project records. MANAGER and EMPLOYEE
 * are intentionally excluded here because they must later receive restricted
 * APIs that return only Projects assigned to them.
 */
@RestController
@RequestMapping("/api/business/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * POST /api/business/projects
     *
     * Creates a Project and connects it to an existing Client, Manager and HR
     * Head. Returns HTTP 201 Created with the saved Project.
     *
     * Allowed roles:
     * ADMIN    - may create records while supervising the complete system.
     * HR_HEAD  - normally creates and assigns Projects during daily operations.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_HEAD')")
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectRequest request) {

        ProjectResponse createdProject =
                projectService.createProject(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdProject);
    }

    /**
     * GET /api/business/projects
     *
     * Returns every Project in the system.
     *
     * Allowed roles: ADMIN and HR_HEAD because this endpoint exposes Projects
     * belonging to every Manager and Client.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_HEAD')")
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {

        return ResponseEntity.ok(projectService.getAllProjects());
    }

    /**
     * GET /api/business/projects/{projectId}
     *
     * Returns one Project using its numeric ID, or HTTP 404 if it does not
     * exist. Manager/Employee ownership checks will be implemented through
     * separate assigned-Project APIs later.
     *
     * Allowed roles: ADMIN and HR_HEAD.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_HEAD')")
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable Integer projectId) {

        return ResponseEntity.ok(
                projectService.getProjectById(projectId));
    }

    /**
     * PUT /api/business/projects/{projectId}
     *
     * Updates Project details and can change the Client, Manager, HR Head,
     * dates, or status. Returns HTTP 404 when the Project does not exist.
     *
     * Allowed roles: ADMIN and HR_HEAD.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_HEAD')")
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Integer projectId,
            @Valid @RequestBody ProjectRequest request) {

        return ResponseEntity.ok(
                projectService.updateProject(projectId, request));
    }

    /**
     * DELETE /api/business/projects/{projectId}
     *
     * Permanently deletes one Project and returns HTTP 204 No Content.
     *
     * Allowed role: ADMIN only. HR_HEAD can update a Project to ON_HOLD or
     * COMPLETED instead of permanently removing historical business data.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Integer projectId) {

        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }
}
