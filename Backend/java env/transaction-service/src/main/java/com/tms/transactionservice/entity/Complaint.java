package com.tms.transactionservice.entity;

import java.time.LocalDateTime;
import com.tms.transactionservice.enums.ComplaintStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Employee issue escalated to their responsible manager for resolution. */
@Entity @Table(name = "complaints") @Getter @Setter @NoArgsConstructor
public class Complaint {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "complaint_id") 
    private Integer complaintId;
    
    @Column(name = "employee_id", nullable = false) 
    private Integer employeeId;
    
    @Column(name = "manager_id") 
    private Integer managerId;
    
    @Column(nullable = false) 
    private String subject;
   
    @Column(columnDefinition = "TEXT") 
    private String description;
   
    @Enumerated(EnumType.STRING) 
    @Column(nullable = false) 
    private ComplaintStatus status = ComplaintStatus.OPEN;
   
    @Column(columnDefinition = "TEXT") 
    private String resolution;
   
    @Column(name = "resolved_at") 
    private LocalDateTime resolvedAt;
   
    @Column(name = "created_at", updatable = false) 
    private LocalDateTime createdAt;
    
    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); }
}
