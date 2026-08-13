package com.tms.businessservice.dto.request;

import java.time.LocalDate;

import com.tms.businessservice.enums.ProjectStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JSON data accepted when an HR Head creates or updates a Project.
 *
 * The frontend/Postman sends related record IDs. The service layer will later
 * load the matching Client, Manager, and HR Head entities from the database.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectRequest {

    @NotBlank(message = "Project name is required")
    @Size(max = 100, message = "Project name cannot exceed 100 characters")
    private String projectName;

    private String description;

    @NotNull(message = "Client ID is required")
    @Positive(message = "Client ID must be a positive number")
    private Integer clientId;

    @NotNull(message = "Manager ID is required")
    @Positive(message = "Manager ID must be a positive number")
    private Integer managerId;

    @NotNull(message = "HR Head ID is required")
    @Positive(message = "HR Head ID must be a positive number")
    private Integer hrHeadId;

    // Dates are optional in the SQL schema.
    private LocalDate startDate;

    private LocalDate endDate;

    /**
     * Optional while creating a Project. The service will use ACTIVE when the
     * request does not provide a status.
     */
    private ProjectStatus status;
}
