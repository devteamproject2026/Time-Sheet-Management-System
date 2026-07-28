package com.tms.businessservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "complaints")
public class Complaints {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "complaint_id")
    private int complaintId;


    @Column(name = "employee_id")
    private int employeeId;


    @Column(name = "manager_id")
    private int managerId;


    @Column(name = "subject")
    private String subject;


    @Column(name = "description")
    private String description;


    @Column(name = "status")
    private String status;


    @Column(name = "resolution")
    private String resolution;


    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;


    @Column(name = "created_at")
    private LocalDateTime createdAt;

   

 

    public Complaints() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    

	public Complaints(int complaintId, int employeeId, int managerId, String subject, String description, String status,
			String resolution, LocalDateTime resolvedAt, LocalDateTime createdAt) {
		super();
		this.complaintId = complaintId;
		this.employeeId = employeeId;
		this.managerId = managerId;
		this.subject = subject;
		this.description = description;
		this.status = status;
		this.resolution = resolution;
		this.resolvedAt = resolvedAt;
		this.createdAt = createdAt;
	}



	public int getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(int complaintId) {
        this.complaintId = complaintId;
    }


    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }


    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }


    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }



    @Override
    public String toString() {
        return "Complaints{" +
                "complaintId=" + complaintId +
                ", employeeId=" + employeeId +
                ", managerId=" + managerId +
                ", subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", resolution='" + resolution + '\'' +
                ", resolvedAt=" + resolvedAt +
                ", createdAt=" + createdAt +
                '}';
    }
}
