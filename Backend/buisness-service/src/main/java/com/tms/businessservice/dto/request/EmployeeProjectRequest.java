package com.tms.businessservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JSON accepted when an Employee is assigned to a Project.
 *
 * The frontend or Postman needs to send only the two related database IDs.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeProjectRequest {

    @NotNull(message = "Employee ID is required")
    @Positive(message = "Employee ID must be a positive number")
    private Integer employeeId;

    @NotNull(message = "Project ID is required")
    @Positive(message = "Project ID must be a positive number")
    private Integer projectId;
}
