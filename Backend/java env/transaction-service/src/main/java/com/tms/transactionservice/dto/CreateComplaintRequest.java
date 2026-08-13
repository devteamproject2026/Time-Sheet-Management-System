package com.tms.transactionservice.dto;
import jakarta.validation.constraints.*;


public record CreateComplaintRequest(
        @NotNull @Positive Integer managerId,
        @NotBlank @Size(max = 100) String subject,
        @NotBlank @Size(max = 4000) String description) {}
