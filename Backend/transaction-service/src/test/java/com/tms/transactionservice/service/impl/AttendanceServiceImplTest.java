package com.tms.transactionservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tms.transactionservice.entity.Attendance;
import com.tms.transactionservice.entity.UserReference;
import com.tms.transactionservice.exception.BusinessRuleException;
import com.tms.transactionservice.repository.AttendanceRepository;
import com.tms.transactionservice.repository.EmployeeProjectReferenceRepository;
import com.tms.transactionservice.service.TransactionResponseMapper;
import com.tms.transactionservice.service.UserAccessService;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceImplTest {

    @Mock UserAccessService userAccess;
    @Mock AttendanceRepository attendance;
    @Mock EmployeeProjectReferenceRepository employeeProjects;
    @Mock TransactionResponseMapper mapper;

    private AttendanceServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AttendanceServiceImpl(
                userAccess, attendance, employeeProjects, mapper);
    }

    @Test
    void employeeCannotCheckInTwiceOnSameDay() {
        UserReference employee = mock(UserReference.class);
        when(employee.getUserId()).thenReturn(7);
        when(userAccess.requireCurrentUser("emp1", "EMPLOYEE"))
                .thenReturn(employee);
        when(attendance.findByEmployeeIdAndAttendanceDate(7, LocalDate.now()))
                .thenReturn(Optional.of(new Attendance()));

        assertThrows(
                BusinessRuleException.class,
                () -> service.checkIn("emp1"));
    }
}
