package com.tms.businessservice.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.tms.businessservice.enums.ProjectStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Safe Project data returned to React or Postman.
 *
 * It contains useful Client and user names but never exposes user passwords or
 * complete internal User entities.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {

    private Integer projectId;

    private String projectName;

    private String description;

    private Integer clientId;

    private String clientName;

    private Integer managerId;

    private String managerUsername;

    private Integer hrHeadId;

    private String hrHeadUsername;

    private LocalDate startDate;

    private LocalDate endDate;

    private ProjectStatus status;

    private LocalDateTime createdAt;
}
