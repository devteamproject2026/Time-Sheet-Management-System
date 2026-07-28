package com.tms.businessservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "timesheets")
public class Timesheets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timesheet_id")
    private int timesheetId;

    @Column(name = "employee_id", nullable = false)
    private int employeeId;

    @Column(name = "task_id", nullable = false)
    private int taskId;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "hours_worked")
    private double hoursWorked;

    @Column(name = "work_description")
    private String workDescription;

    @Column(name = "status")
    private String status;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;


    // Default Constructor
    public Timesheets() {
    }


    // Parameterized Constructor
    public Timesheets(int timesheetId, int employeeId, int taskId,
                      LocalDate workDate, double hoursWorked,
                      String workDescription, String status,
                      LocalDateTime submittedAt) {

        this.timesheetId = timesheetId;
        this.employeeId = employeeId;
        this.taskId = taskId;
        this.workDate = workDate;
        this.hoursWorked = hoursWorked;
        this.workDescription = workDescription;
        this.status = status;
        this.submittedAt = submittedAt;
    }


    // Getters and Setters

    public int getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(int timesheetId) {
        this.timesheetId = timesheetId;
    }


    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }


    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }


    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }


    public double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }


    public String getWorkDescription() {
        return workDescription;
    }

    public void setWorkDescription(String workDescription) {
        this.workDescription = workDescription;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }


    // toString Method

    @Override
    public String toString() {
        return "Timesheets{" +
                "timesheetId=" + timesheetId +
                ", employeeId=" + employeeId +
                ", taskId=" + taskId +
                ", workDate=" + workDate +
                ", hoursWorked=" + hoursWorked +
                ", workDescription='" + workDescription + '\'' +
                ", status='" + status + '\'' +
                ", submittedAt=" + submittedAt +
                '}';
    }
}
