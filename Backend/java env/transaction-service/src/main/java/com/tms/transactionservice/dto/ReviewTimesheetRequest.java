package com.tms.transactionservice.dto;
import com.tms.transactionservice.enums.TimesheetStatus;
import jakarta.validation.constraints.*;
public record ReviewTimesheetRequest(
		@NotNull TimesheetStatus decision, 
		@Size(max=500) String comments) {}
