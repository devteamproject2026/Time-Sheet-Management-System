package com.tms.businessservice.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents one assignment from the employee_projects database table.
 *
 * This is a link entity: it connects one Employee user to one Project. The same
 * Employee cannot be assigned to the same Project more than once.
 */
@Entity
@Table(
        name = "employee_projects",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_employee_project",
                columnNames = {"employee_id", "project_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_project_id")
    private Integer employeeProjectId;

    /**
     * Many assignment rows may reference the same Employee.
     *
     * The service layer will later verify that this user has EMPLOYEE role and
     * has an approved, active account.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Users employee;

    /**
     * Many Employees may be assigned to the same Project.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // Hibernate fills this value when a new assignment is saved.
    @CreationTimestamp
    @Column(name = "assigned_date", updatable = false)
    private LocalDateTime assignedDate;
}
