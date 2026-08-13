package com.tms.transactionservice.repository;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.tms.transactionservice.entity.Timesheet;
public interface TimesheetRepository extends JpaRepository<Timesheet, Integer> {
    List<Timesheet> findByEmployeeIdOrderBySubmittedAtDesc(Integer employeeId);

    boolean existsByEmployeeIdAndTaskIdAndWorkDate(
            Integer employeeId,
            Integer taskId,
            LocalDate workDate);

    @Query("""
            select coalesce(sum(timesheet.hoursWorked), 0)
            from Timesheet timesheet
            where timesheet.employeeId = :employeeId
              and timesheet.workDate = :workDate
            """)
    BigDecimal sumHoursForEmployeeOnDate(
            @Param("employeeId") Integer employeeId,
            @Param("workDate") LocalDate workDate);

    @Query("""
            select timesheet
            from Timesheet timesheet
            where timesheet.taskId in (
                select task.taskId
                from Task task
                where task.managerId = :managerId
            )
            order by timesheet.submittedAt desc
            """)
    List<Timesheet> findAllForManager(@Param("managerId") Integer managerId);

    List<Timesheet> findByTaskIdIn(List<Integer> taskIds);
}
