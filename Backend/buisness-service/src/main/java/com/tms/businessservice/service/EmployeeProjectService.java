package com.tms.businessservice.service;

import java.util.List;

import com.tms.businessservice.dto.request.EmployeeProjectRequest;
import com.tms.businessservice.dto.response.EmployeeProjectResponse;

/**
 * Defines operations for assigning Employees to Projects.
 *
 * actorUsername is the verified username from Spring Security. It allows the
 * service to enforce Manager ownership instead of trusting a request body.
 */
public interface EmployeeProjectService {

    EmployeeProjectResponse assignEmployee(
            EmployeeProjectRequest request,
            String actorUsername);

    List<EmployeeProjectResponse> getAllAssignments(String actorUsername);

    List<EmployeeProjectResponse> getAssignmentsByProject(
            Integer projectId,
            String actorUsername);

    List<EmployeeProjectResponse> getAssignmentsByEmployee(
            Integer employeeId,
            String actorUsername);

    List<EmployeeProjectResponse> getMyAssignments(String actorUsername);

    void removeAssignment(
            Integer employeeProjectId,
            String actorUsername);
}
