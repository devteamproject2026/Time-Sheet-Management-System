package com.tms.transactionservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tms.transactionservice.entity.ProjectReference;
public interface ProjectReferenceRepository extends JpaRepository<ProjectReference, Integer> {}
