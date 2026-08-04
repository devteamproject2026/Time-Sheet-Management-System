package com.tms.transactionservice.dto;
import java.time.LocalDate;
import jakarta.validation.constraints.*;


public record CreateTaskRequest(@NotNull Integer projectId, @NotNull Integer employeeId, @NotBlank @Size(max=100) String taskName,
    String taskDescription, LocalDate startDate, LocalDate endDate) {}
