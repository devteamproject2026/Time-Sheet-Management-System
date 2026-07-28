package com.tms.businessservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "employee_projects")
public class Employee_Projects {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_project_id")
    private int employeeProjectId;


    @Column(name = "employee_id")
    private int employeeId;


    @Column(name = "project_id")
    private int projectId;


    @Column(name = "assigned_date")
    private LocalDateTime assignedDate;


   
    

    public Employee_Projects() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    

	public Employee_Projects(int employeeProjectId, int employeeId, int projectId, LocalDateTime assignedDate) {
		super();
		this.employeeProjectId = employeeProjectId;
		this.employeeId = employeeId;
		this.projectId = projectId;
		this.assignedDate = assignedDate;
	}



	public int getEmployeeProjectId() {
        return employeeProjectId;
    }

    public void setEmployeeProjectId(int employeeProjectId) {
        this.employeeProjectId = employeeProjectId;
    }


    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }


    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }


    public LocalDateTime getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDateTime assignedDate) {
        this.assignedDate = assignedDate;
    }



    @Override
    public String toString() {
        return "EmployeeProjects{" +
                "employeeProjectId=" + employeeProjectId +
                ", employeeId=" + employeeId +
                ", projectId=" + projectId +
                ", assignedDate=" + assignedDate +
                '}';
    }
}
