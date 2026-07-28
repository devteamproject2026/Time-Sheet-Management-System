package com.tms.businessservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "timesheet_approvals")
public class Timesheet_Approvals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_id")
    private int approvalId;


    @Column(name = "timesheet_id")
    private int timesheetId;


    @Column(name = "manager_id")
    private int managerId;


    @Column(name = "approval_status")
    private String approvalStatus;


    @Column(name = "comments")
    private String comments;


    @Column(name = "approval_date")
    private LocalDateTime approvalDate;
    
    

    public Timesheet_Approvals() {
		super();
		// TODO Auto-generated constructor stub
	}

    
	public Timesheet_Approvals(int approvalId, int timesheetId, int managerId, String approvalStatus, String comments,
			LocalDateTime approvalDate) {
		super();
		this.approvalId = approvalId;
		this.timesheetId = timesheetId;
		this.managerId = managerId;
		this.approvalStatus = approvalStatus;
		this.comments = comments;
		this.approvalDate = approvalDate;
	}


	public int getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(int approvalId) {
        this.approvalId = approvalId;
    }


    public int getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(int timesheetId) {
        this.timesheetId = timesheetId;
    }


    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }


    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }


    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }



    @Override
    public String toString() {
        return "TimesheetApprovals{" +
                "approvalId=" + approvalId +
                ", timesheetId=" + timesheetId +
                ", managerId=" + managerId +
                ", approvalStatus='" + approvalStatus + '\'' +
                ", comments='" + comments + '\'' +
                ", approvalDate=" + approvalDate +
                '}';
    }
}
