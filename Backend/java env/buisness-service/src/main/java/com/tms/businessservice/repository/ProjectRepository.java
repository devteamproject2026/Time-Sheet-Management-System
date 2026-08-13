package com.tms.businessservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tms.businessservice.entity.Project;

import org.springframework.data.repository.query.Param;

/**
 * Provides standard database operations for Project.
 *
 * JpaRepository already supplies save, findById, findAll, existsById and
 * deleteById, so we do not write SQL for basic CRUD operations.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    /**
     * Returns only Projects whose manager_id belongs to the logged-in Manager.
     *
     * Spring Data follows the Project.manager relationship and matches the
     * Manager's username without handwritten SQL.
     */
    List<Project> findByManager_UsernameOrderByCreatedAtDesc(
            String managerUsername);

    /**
     * Returns Projects connected to the logged-in Employee through the
     * employee_projects assignment table.
     */
    @Query("""
            SELECT assignment.project
            FROM EmployeeProject assignment
            WHERE assignment.employee.username = :employeeUsername
            ORDER BY assignment.assignedDate DESC
            """)
    List<Project> findAssignedProjectsByEmployeeUsername(
            @Param("employeeUsername") String employeeUsername);
}
