package com.tms.transactionservice.dto.response;

import java.time.LocalDateTime;

import com.tms.transactionservice.enums.ComplaintStatus;

public record ComplaintResponse(
        Integer complaintId,
        Integer employeeId,
        String employeeUsername,
        String employeeFullName,
        Integer managerId,
        String managerUsername,
        String managerFullName,
        String subject,
        String description,
        ComplaintStatus status,
        String resolution,
        LocalDateTime resolvedAt,
        LocalDateTime createdAt) {}
