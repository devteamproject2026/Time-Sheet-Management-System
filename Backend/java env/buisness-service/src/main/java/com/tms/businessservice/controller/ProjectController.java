package com.tms.businessservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
 * HR_HEAD creates and updates normal Project business data. ADMIN and HR_HEAD
 * can read all Projects. Permanent deletion is intentionally not exposed by
 * the current requirements. MANAGER and EMPLOYEE receive separate APIs that
 * return only their own Projects.
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
     * Allowed role: HR_HEAD only.
     *
     * HR Head performs daily Project setup and Manager assignment. ADMIN keeps
     * read access for supervision instead of changing operational data.
     */
    @PreAuthorize("hasRole('HR_HEAD')")
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
     * GET /api/business/projects/my-managed-projects
     *
     * Returns only Projects whose manager_id belongs to the logged-in Manager.
     * No Manager ID is accepted from the URL, preventing one Manager from
     * requesting another Manager's Projects.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/my-managed-projects")
    public ResponseEntity<List<ProjectResponse>> getMyManagedProjects(
            Authentication authentication) {

        return ResponseEntity.ok(
                projectService.getMyManagedProjects(
                        authentication.getName()));
    }

    /**
     * GET /api/business/projects/my-assigned-projects
     *
     * Returns only Projects connected to the logged-in Employee through the
     * employee_projects table. The Employee identity comes from the JWT.
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my-assigned-projects")
    public ResponseEntity<List<ProjectResponse>> getMyAssignedProjects(
            Authentication authentication) {

        return ResponseEntity.ok(
                projectService.getMyAssignedProjects(
                        authentication.getName()));
    }

    /**
     * GET /api/business/projects/{projectId}
     *
     * Returns one Project using its numeric ID, or HTTP 404 if it does not
     * exist. Manager and Employee use the separate scoped APIs above instead
     * of accessing arbitrary Project IDs.
     *
     * Allowed roles: ADMIN and HR_HEAD.
     *
     * HR Head uses this for daily work, while ADMIN has read-only access for
     * supervision and auditing.
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
     * Allowed role: HR_HEAD only.
     *
     * HR Head maintains Project business details. ADMIN retains read access for
     * supervision but does not change operational data.
     */
    @PreAuthorize("hasRole('HR_HEAD')")
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Integer projectId,
            @Valid @RequestBody ProjectRequest request) {

        return ResponseEntity.ok(
                projectService.updateProject(projectId, request));
    }

}
