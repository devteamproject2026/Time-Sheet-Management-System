package com.tms.transactionservice.dto.response;

import java.math.BigDecimal;

public record EmployeeReportResponse(
        Integer employeeId,
        String employeeUsername,
        String employeeFullName,
        long totalTasks,
        long completedTasks,
        double averageProgress,
        BigDecimal totalApprovedHours,
        long pendingTimesheets,
        long approvedTimesheets,
        long rejectedTimesheets) {}
