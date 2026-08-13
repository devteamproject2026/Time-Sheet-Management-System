package com.tms.transactionservice.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.transactionservice.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    Optional<Attendance> findByEmployeeIdAndAttendanceDate(
            Integer employeeId,
            LocalDate attendanceDate);

    List<Attendance> findByEmployeeIdOrderByAttendanceDateDesc(Integer employeeId);

    List<Attendance> findByEmployeeIdInAndAttendanceDateOrderByEmployeeId(
            List<Integer> employeeIds,
            LocalDate attendanceDate);
}
