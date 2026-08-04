package com.tms.transactionservice.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tms.transactionservice.entity.Timesheet;
public interface TimesheetRepository extends JpaRepository<Timesheet, Integer> { List<Timesheet> findByEmployeeIdOrderBySubmittedAtDesc(Integer employeeId); }
