package com.tms.transactionservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

/** Read-only project reference. Business Service remains the project owner. */
@Entity @Immutable @Table(name = "projects") @Getter
public class ProjectReference {
	
    @Id 
    @Column(name = "project_id") 
    private Integer projectId;
    
    @Column(name = "project_name") 
    private String projectName;
    
    @Column(name = "manager_id") 
    private Integer managerId;

    @Column(name = "status")
    private String status;
}
