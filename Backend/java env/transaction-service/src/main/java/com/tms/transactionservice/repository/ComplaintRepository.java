package com.tms.transactionservice.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tms.transactionservice.entity.Complaint;
public interface ComplaintRepository extends JpaRepository<Complaint, Integer> { List<Complaint> findByEmployeeIdOrderByCreatedAtDesc(Integer employeeId); List<Complaint> findByManagerIdOrderByCreatedAtDesc(Integer managerId); }
