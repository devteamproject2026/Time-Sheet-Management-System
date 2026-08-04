package com.tms.transactionservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tms.transactionservice.entity.EmployeeProjectReference;


public interface EmployeeProjectReferenceRepository extends JpaRepository<EmployeeProjectReference, Integer> { 
	boolean existsByEmployeeIdAndProjectId(Integer employeeId, Integer projectId); 
}
