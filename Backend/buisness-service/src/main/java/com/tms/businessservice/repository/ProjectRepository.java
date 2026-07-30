package com.tms.businessservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.businessservice.entity.Project;

/**
 * Provides standard database operations for Project.
 *
 * JpaRepository already supplies save, findById, findAll, existsById and
 * deleteById, so we do not write SQL for basic CRUD operations.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
}
