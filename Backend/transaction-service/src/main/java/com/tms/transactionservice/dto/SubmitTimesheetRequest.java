package com.tms.transactionservice.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
public record SubmitTimesheetRequest(@NotNull @Positive Integer taskId,
		@NotNull @PastOrPresent LocalDate workDate,
		@NotNull @DecimalMin(value="0.25") @DecimalMax(value="24.00") BigDecimal hoursWorked, 
		@NotBlank @Size(max = 4000) String workDescription) {}
