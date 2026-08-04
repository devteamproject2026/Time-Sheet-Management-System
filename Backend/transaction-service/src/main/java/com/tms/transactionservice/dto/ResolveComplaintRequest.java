package com.tms.transactionservice.dto;
import jakarta.validation.constraints.NotBlank;
public record ResolveComplaintRequest(@NotBlank String resolution) {}
