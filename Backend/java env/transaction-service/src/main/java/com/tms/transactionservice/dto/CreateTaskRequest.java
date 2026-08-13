package com.tms.transactionservice.dto;
import java.time.LocalDate;
import jakarta.validation.constraints.*;


public record CreateTaskRequest(
    @NotNull @Positive Integer projectId,
    @NotNull @Positive Integer employeeId,
    @NotBlank @Size(max=100) String taskName,
    @Size(max=4000) String taskDescription,
    LocalDate startDate,
    LocalDate endDate) {}
