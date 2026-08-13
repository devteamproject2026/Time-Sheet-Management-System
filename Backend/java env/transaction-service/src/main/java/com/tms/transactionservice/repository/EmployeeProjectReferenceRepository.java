package com.tms.transactionservice.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.tms.transactionservice.entity.EmployeeProjectReference;


public interface EmployeeProjectReferenceRepository extends JpaRepository<EmployeeProjectReference, Integer> { 
	boolean existsByEmployeeIdAndProjectId(Integer employeeId, Integer projectId);

    @Query("""
            select distinct ep.employeeId
            from EmployeeProjectReference ep
            where ep.projectId in (
                select project.projectId
                from ProjectReference project
                where project.managerId = :managerId
            )
            """)
    List<Integer> findEmployeeIdsManagedBy(@Param("managerId") Integer managerId);

    @Query("""
            select distinct project.managerId
            from ProjectReference project
            where project.projectId in (
                select ep.projectId
                from EmployeeProjectReference ep
                where ep.employeeId = :employeeId
            )
            """)
    List<Integer> findManagerIdsForEmployee(@Param("employeeId") Integer employeeId);

    @Query("""
            select count(ep)
            from EmployeeProjectReference ep
            where ep.employeeId = :employeeId
              and ep.projectId in (
                  select project.projectId
                  from ProjectReference project
                  where project.managerId = :managerId
              )
            """)
    long countAssignmentsWithManager(
            @Param("employeeId") Integer employeeId,
            @Param("managerId") Integer managerId);
}
