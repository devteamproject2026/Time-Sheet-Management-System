package com.tms.transactionservice.entity;

import java.time.*;
import com.tms.transactionservice.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A manager-assigned, employee-owned unit of work on an existing project. */
@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
public class Task {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "task_id")
	private Integer taskId;
	
	@Column(name = "project_id", nullable = false)
	private Integer projectId;
	
	@Column(name = "manager_id", nullable = false)
	private Integer managerId;
	
	@Column(name = "employee_id", nullable = false)
	private Integer employeeId;
	
	@Column(name = "task_name", nullable = false)
	private String taskName;
	
	@Column(name = "task_description", columnDefinition = "TEXT")
	private String taskDescription;
	private LocalDate startDate;
	private LocalDate endDate;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TaskStatus status = TaskStatus.ASSIGNED;
	
	@Column(name = "progress_percent", nullable = false)
	private Integer progressPercent = 0;
	
	@Column(columnDefinition = "TEXT")
	private String remarks;
	
	@Column(name = "last_updated")
	private LocalDateTime lastUpdated;
	
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	void onCreate() {
		createdAt = LocalDateTime.now();
		lastUpdated = createdAt;
	}

	@PreUpdate
	void onUpdate() {
		lastUpdated = LocalDateTime.now();
	}
}
