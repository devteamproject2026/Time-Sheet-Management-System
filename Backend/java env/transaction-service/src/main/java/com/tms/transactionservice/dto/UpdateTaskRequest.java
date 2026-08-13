package com.tms.transactionservice.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Complete editable Task data accepted from the owning Manager. */
public record UpdateTaskRequest(
        @NotNull @Positive Integer employeeId,
        @NotBlank @Size(max = 100) String taskName,
        @Size(max = 4000) String taskDescription,
        LocalDate startDate,
        LocalDate endDate) {}
