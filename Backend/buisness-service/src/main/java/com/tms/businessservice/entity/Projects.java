package com.tms.businessservice.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "projects")
public class Projects {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private int projectId;


    @Column(name = "project_name")
    private String projectName;


    @Column(name = "description")
    private String description;


    @Column(name = "client_id")
    private int clientId;


    @Column(name = "manager_id")
    private int managerId;


    @Column(name = "hr_head_id")
    private int hrHeadId;


    @Column(name = "start_date")
    private LocalDate startDate;


    @Column(name = "end_date")
    private LocalDate endDate;


    @Column(name = "status")
    private String status;


    @Column(name = "created_at")
    private LocalDateTime createdAt;

   

    public Projects() {
		super();
		// TODO Auto-generated constructor stub
	}

    
	public Projects(int projectId, String projectName, String description, int clientId, int managerId, int hrHeadId,
			LocalDate startDate, LocalDate endDate, String status, LocalDateTime createdAt) {
		super();
		this.projectId = projectId;
		this.projectName = projectName;
		this.description = description;
		this.clientId = clientId;
		this.managerId = managerId;
		this.hrHeadId = hrHeadId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = status;
		this.createdAt = createdAt;
	}


	public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }


    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }


    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }


    public int getHrHeadId() {
        return hrHeadId;
    }

    public void setHrHeadId(int hrHeadId) {
        this.hrHeadId = hrHeadId;
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


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }



    @Override
    public String toString() {
        return "Projects{" +
                "projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", description='" + description + '\'' +
                ", clientId=" + clientId +
                ", managerId=" + managerId +
                ", hrHeadId=" + hrHeadId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
