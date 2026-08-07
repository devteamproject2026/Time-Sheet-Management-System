package com.tms.transactionservice.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tms.transactionservice.entity.Task;
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByEmployeeIdOrderByLastUpdatedDesc(Integer employeeId);
    List<Task> findByManagerIdOrderByLastUpdatedDesc(Integer managerId);
    List<Task> findByManagerIdAndEmployeeIdOrderByLastUpdatedDesc(
            Integer managerId,
            Integer employeeId);
}
