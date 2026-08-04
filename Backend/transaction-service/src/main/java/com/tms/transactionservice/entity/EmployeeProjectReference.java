package com.tms.transactionservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

/** Proves that an employee can receive work on a project. Owned by Business Service. */
@Entity @Immutable @Table(name = "employee_projects") @Getter
public class EmployeeProjectReference {
	
    @Id 
    @Column(name = "employee_project_id") 
    private Integer employeeProjectId;
    
    @Column(name = "employee_id") 
    private Integer employeeId;
    
    @Column(name = "project_id") 
    private Integer projectId;
}
