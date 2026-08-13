package com.tms.businessservice.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.tms.businessservice.enums.ProjectStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents one row from the projects database table.
 *
 * A Project belongs to one Client and is controlled by one Manager and one HR
 * Head. The API will receive their numeric IDs; JPA uses these relationships to
 * enforce the foreign keys defined in the SQL database.
 */
@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Integer projectId;

    @Column(name = "project_name", nullable = false, length = 100)
    private String projectName;

    // MySQL TEXT is suitable for a description longer than a normal VARCHAR.
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Many Projects can belong to the same Client.
     *
     * LAZY means the Client is loaded only when its details are actually used.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    /**
     * The user assigned to manage this Project.
     *
     * The service layer will later verify that this user has MANAGER role.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "manager_id", nullable = false)
    private Users manager;

    /**
     * The HR Head responsible for creating or supervising this Project.
     *
     * The service layer will later verify that this user has HR_HEAD role.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hr_head_id", nullable = false)
    private Users hrHead;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * EnumType.STRING stores readable values such as ACTIVE in MySQL instead of
     * unsafe numeric positions such as 0 or 1.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.ACTIVE;

    // Hibernate fills this timestamp when a new Project is saved.
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
