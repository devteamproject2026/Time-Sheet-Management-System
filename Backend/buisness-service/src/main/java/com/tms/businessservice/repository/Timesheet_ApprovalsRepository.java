package com.tms.businessservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.businessservice.entity.Timesheet_Approvals;

@Repository
public interface Timesheet_ApprovalsRepository extends JpaRepository<Timesheet_Approvals, Integer> {

}
