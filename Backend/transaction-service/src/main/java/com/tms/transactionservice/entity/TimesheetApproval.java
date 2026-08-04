package com.tms.transactionservice.entity;

import java.time.LocalDateTime;
import com.tms.transactionservice.enums.TimesheetStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Immutable audit record of a manager's approval or rejection decision. */
@Entity
@Table(name = "timesheet_approvals")
@Getter
@Setter
@NoArgsConstructor
public class TimesheetApproval {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "approval_id")
	private Integer approvalId;
	@Column(name = "timesheet_id", nullable = false)
	private Integer timesheetId;
	@Column(name = "manager_id", nullable = false)
	private Integer managerId;
	@Enumerated(EnumType.STRING)
	@Column(name = "approval_status", nullable = false)
	private TimesheetStatus approvalStatus;
	@Column(length = 500)
	private String comments;
	@Column(name = "approval_date", updatable = false)
	private LocalDateTime approvalDate;

	@PrePersist
	void onCreate() {
		approvalDate = LocalDateTime.now();
	}
}
