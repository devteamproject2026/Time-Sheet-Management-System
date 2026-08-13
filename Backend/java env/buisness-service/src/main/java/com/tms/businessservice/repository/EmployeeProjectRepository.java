package com.tms.businessservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.businessservice.entity.EmployeeProject;

/**
 * Provides standard database operations for Employee-Project assignments.
 *
 * Spring Data builds these queries from the method names, so handwritten SQL is
 * not required.
 */
@Repository
public interface EmployeeProjectRepository
        extends JpaRepository<EmployeeProject, Integer> {

    /**
     * Used before insert to give a clear duplicate-assignment error.
     */
    boolean existsByEmployee_UserIdAndProject_ProjectId(
            Integer employeeId,
            Integer projectId);

    /**
     * Returns the team assigned to one Project, newest assignment first.
     */
    List<EmployeeProject> findByProject_ProjectIdOrderByAssignedDateDesc(
            Integer projectId);

    /**
     * Returns every Project assigned to one Employee.
     */
    List<EmployeeProject> findByEmployee_UserIdOrderByAssignedDateDesc(
            Integer employeeId);
}
