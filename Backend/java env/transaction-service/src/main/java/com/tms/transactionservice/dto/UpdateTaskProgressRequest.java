package com.tms.transactionservice.dto;
import jakarta.validation.constraints.*;
public record UpdateTaskProgressRequest(
		@NotNull @Min(0) @Max(100) Integer progressPercent, 
		@Size(max=4000) String remarks) {
	
}
