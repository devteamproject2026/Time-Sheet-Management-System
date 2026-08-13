package com.tms.transactionservice.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.tms.transactionservice.enums.TimesheetStatus;

public record TimesheetResponse(
        Integer timesheetId,
        Integer employeeId,
        String employeeUsername,
        String employeeFullName,
        Integer taskId,
        String taskName,
        Integer projectId,
        String projectName,
        LocalDate workDate,
        BigDecimal hoursWorked,
        String workDescription,
        TimesheetStatus status,
        LocalDateTime submittedAt) {}
