package com.tms.transactionservice.dto;
import jakarta.validation.constraints.*;


public record CreateComplaintRequest(
		@NotNull Integer managerId, 
		@NotBlank @Size(max=100) String subject, 
		@NotBlank String description) {}
