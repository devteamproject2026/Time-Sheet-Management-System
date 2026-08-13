package com.tms.transactionservice.entity;

import java.math.BigDecimal;
import java.time.*;
import com.tms.transactionservice.enums.TimesheetStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Employee's submitted hours against a task; manager review changes its status.
 */
@Entity
@Table(name = "timesheets")
@Getter
@Setter
@NoArgsConstructor
public class Timesheet {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "timesheet_id")
	private Integer timesheetId;
	@Column(name = "employee_id", nullable = false)
	private Integer employeeId;
	@Column(name = "task_id", nullable = false)
	private Integer taskId;
	@Column(name = "work_date", nullable = false)
	private LocalDate workDate;
	@Column(name = "hours_worked", nullable = false, precision = 4, scale = 2)
	private BigDecimal hoursWorked;
	@Column(name = "work_description", columnDefinition = "TEXT")
	private String workDescription;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TimesheetStatus status = TimesheetStatus.PENDING;
	@Column(name = "submitted_at", updatable = false)
	private LocalDateTime submittedAt;

	@PrePersist
	void onCreate() {
		submittedAt = LocalDateTime.now();
	}
}
