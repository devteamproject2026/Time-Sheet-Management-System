package com.tms.businessservice.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Safe assignment information returned to React or Postman.
 *
 * It includes useful names for display but never exposes the User password or
 * the complete internal entities.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeProjectResponse {

    private Integer employeeProjectId;

    private Integer employeeId;

    private String employeeUsername;

    private String employeeFullName;

    private Integer projectId;

    private String projectName;

    private Integer managerId;

    private String managerUsername;

    private LocalDateTime assignedDate;
}
