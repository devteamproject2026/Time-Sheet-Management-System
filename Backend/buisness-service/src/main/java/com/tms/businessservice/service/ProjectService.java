package com.tms.businessservice.service;

import java.util.List;

import com.tms.businessservice.dto.request.ProjectRequest;
import com.tms.businessservice.dto.response.ProjectResponse;

/**
 * Defines the business operations available for Projects.
 *
 * The REST controller added in the next step will call this interface instead
 * of communicating directly with database repositories.
 */
public interface ProjectService {

    ProjectResponse createProject(ProjectRequest request);

    List<ProjectResponse> getAllProjects();

    List<ProjectResponse> getMyManagedProjects(String managerUsername);

    List<ProjectResponse> getMyAssignedProjects(String employeeUsername);

    ProjectResponse getProjectById(Integer projectId);

    ProjectResponse updateProject(
            Integer projectId,
            ProjectRequest request);
}
