package com.tms.transactionservice.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.tms.transactionservice.enums.TaskStatus;

public record TaskResponse(
        Integer taskId,
        Integer projectId,
        String projectName,
        Integer managerId,
        String managerUsername,
        Integer employeeId,
        String employeeUsername,
        String employeeFullName,
        String taskName,
        String taskDescription,
        LocalDate startDate,
        LocalDate endDate,
        TaskStatus status,
        Integer progressPercent,
        String remarks,
        LocalDateTime lastUpdated,
        LocalDateTime createdAt) {}
