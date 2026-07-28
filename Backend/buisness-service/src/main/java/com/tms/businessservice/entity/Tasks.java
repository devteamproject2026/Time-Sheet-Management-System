package com.tms.businessservice.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Tasks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private int taskId;


    @Column(name = "project_id")
    private int projectId;


    @Column(name = "manager_id")
    private int managerId;


    @Column(name = "employee_id")
    private int employeeId;


    @Column(name = "task_name")
    private String taskName;


    @Column(name = "task_description")
    private String taskDescription;


    @Column(name = "start_date")
    private LocalDate startDate;


    @Column(name = "end_date")
    private LocalDate endDate;


    @Column(name = "status")
    private String status;


    @Column(name = "progress_percent")
    private int progressPercent;


    @Column(name = "remarks")
    private String remarks;


    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;


    @Column(name = "created_at")
    private LocalDateTime createdAt;

   

    public Tasks() {
		super();
		// TODO Auto-generated constructor stub
	}

    
	public Tasks(int taskId, int projectId, int managerId, int employeeId, String taskName, String taskDescription,
			LocalDate startDate, LocalDate endDate, String status, int progressPercent, String remarks,
			LocalDateTime lastUpdated, LocalDateTime createdAt) {
		super();
		this.taskId = taskId;
		this.projectId = projectId;
		this.managerId = managerId;
		this.employeeId = employeeId;
		this.taskName = taskName;
		this.taskDescription = taskDescription;
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = status;
		this.progressPercent = progressPercent;
		this.remarks = remarks;
		this.lastUpdated = lastUpdated;
		this.createdAt = createdAt;
	}


	public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }


    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }


    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }


    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }


    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }


    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }


    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }


    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public int getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(int progressPercent) {
        this.progressPercent = progressPercent;
    }


    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }



    @Override
    public String toString() {
        return "Tasks{" +
                "taskId=" + taskId +
                ", projectId=" + projectId +
                ", managerId=" + managerId +
                ", employeeId=" + employeeId +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                ", progressPercent=" + progressPercent +
                ", remarks='" + remarks + '\'' +
                ", lastUpdated=" + lastUpdated +
                ", createdAt=" + createdAt +
                '}';
    }
}
