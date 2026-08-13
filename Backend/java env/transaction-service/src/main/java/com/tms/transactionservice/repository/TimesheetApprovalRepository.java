package com.tms.transactionservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tms.transactionservice.entity.TimesheetApproval;
public interface TimesheetApprovalRepository extends JpaRepository<TimesheetApproval, Integer> {}
