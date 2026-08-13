package com.tms.businessservice.service;

import java.util.List;

import com.tms.businessservice.dto.request.EmployeeProjectRequest;
import com.tms.businessservice.dto.response.EmployeeProjectResponse;

/**
 * Defines operations for assigning Employees to Projects.
 *
 * actorUsername is the verified username from Spring Security. The service
 * uses it to validate the current account and to enforce Manager ownership
 * for the read-only Project-team operation.
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
