package com.tms.transactionservice.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tms.transactionservice.dto.response.AttendanceResponse;
import com.tms.transactionservice.entity.Attendance;
import com.tms.transactionservice.entity.UserReference;
import com.tms.transactionservice.enums.AttendanceStatus;
import com.tms.transactionservice.exception.BusinessRuleException;
import com.tms.transactionservice.repository.AttendanceRepository;
import com.tms.transactionservice.repository.EmployeeProjectReferenceRepository;
import com.tms.transactionservice.service.AttendanceService;
import com.tms.transactionservice.service.TransactionResponseMapper;
import com.tms.transactionservice.service.UserAccessService;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private static final long HALF_DAY_THRESHOLD_MINUTES = 240;

    private final UserAccessService userAccess;
    private final AttendanceRepository attendanceRepository;
    private final EmployeeProjectReferenceRepository employeeProjects;
    private final TransactionResponseMapper mapper;

    public AttendanceServiceImpl(
            UserAccessService userAccess,
            AttendanceRepository attendanceRepository,
            EmployeeProjectReferenceRepository employeeProjects,
            TransactionResponseMapper mapper) {
        this.userAccess = userAccess;
        this.attendanceRepository = attendanceRepository;
        this.employeeProjects = employeeProjects;
        this.mapper = mapper;
    }

    @Override
    public AttendanceResponse checkIn(String username) {
        UserReference employee = userAccess.requireCurrentUser(username, "EMPLOYEE");
        LocalDate today = LocalDate.now();

        if (attendanceRepository.findByEmployeeIdAndAttendanceDate(
                employee.getUserId(), today).isPresent()) {
            throw new BusinessRuleException("Attendance is already recorded for today");
        }

        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employee.getUserId());
        attendance.setAttendanceDate(today);
        attendance.setCheckIn(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        attendance.setStatus(AttendanceStatus.PRESENT);

        return mapper.toAttendanceResponse(attendanceRepository.save(attendance));
    }

    @Override
    public AttendanceResponse checkOut(String username) {
        UserReference employee = userAccess.requireCurrentUser(username, "EMPLOYEE");
        Attendance attendance = attendanceRepository
                .findByEmployeeIdAndAttendanceDate(employee.getUserId(), LocalDate.now())
                .orElseThrow(() -> new BusinessRuleException(
                        "Check in before checking out"));

        if (attendance.getCheckOut() != null) {
            throw new BusinessRuleException("Check-out is already recorded for today");
        }

        LocalTime checkOut = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
        if (checkOut.isBefore(attendance.getCheckIn())) {
            throw new BusinessRuleException("Check-out cannot be before check-in");
        }

        attendance.setCheckOut(checkOut);
        long workedMinutes = Duration.between(attendance.getCheckIn(), checkOut).toMinutes();
        attendance.setStatus(workedMinutes < HALF_DAY_THRESHOLD_MINUTES
                ? AttendanceStatus.HALF_DAY
                : AttendanceStatus.PRESENT);

        return mapper.toAttendanceResponse(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> myAttendance(String username) {
        Integer employeeId = userAccess
                .requireCurrentUser(username, "EMPLOYEE")
                .getUserId();
        return attendanceRepository.findByEmployeeIdOrderByAttendanceDateDesc(employeeId)
                .stream()
                .map(mapper::toAttendanceResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> teamAttendance(String username, LocalDate date) {
        Integer managerId = userAccess
                .requireCurrentUser(username, "MANAGER")
                .getUserId();
        List<Integer> employeeIds = employeeProjects.findEmployeeIdsManagedBy(managerId);
        if (employeeIds.isEmpty()) return List.of();

        LocalDate requestedDate = date == null ? LocalDate.now() : date;
        return attendanceRepository
                .findByEmployeeIdInAndAttendanceDateOrderByEmployeeId(
                        employeeIds, requestedDate)
                .stream()
                .map(mapper::toAttendanceResponse)
                .toList();
    }
}
