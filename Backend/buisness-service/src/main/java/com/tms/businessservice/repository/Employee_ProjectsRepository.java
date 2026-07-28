package com.tms.businessservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.businessservice.entity.Employee_Projects;

@Repository
public interface Employee_ProjectsRepository extends JpaRepository<Employee_Projects, Integer> {

}
