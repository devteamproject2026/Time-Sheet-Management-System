package com.tms.transactionservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tms.transactionservice.dto.SubmitTimesheetRequest;
import com.tms.transactionservice.entity.Task;
import com.tms.transactionservice.entity.UserReference;
import com.tms.transactionservice.enums.TaskStatus;
import com.tms.transactionservice.exception.BusinessRuleException;
import com.tms.transactionservice.repository.TaskRepository;
import com.tms.transactionservice.repository.TimesheetRepository;
import com.tms.transactionservice.service.TransactionResponseMapper;
import com.tms.transactionservice.service.UserAccessService;

@ExtendWith(MockitoExtension.class)
class TimesheetServiceImplTest {

    @Mock UserAccessService userAccess;
    @Mock TaskRepository tasks;
    @Mock TimesheetRepository timesheets;
    @Mock TransactionResponseMapper mapper;

    private TimesheetServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TimesheetServiceImpl(userAccess, tasks, timesheets, mapper);
    }

    @Test
    void submitRejectsDuplicateTaskAndDate() {
        LocalDate date = LocalDate.now();
        UserReference employee = mock(UserReference.class);
        when(employee.getUserId()).thenReturn(7);
        Task task = task(10, 7);
        when(userAccess.requireCurrentUser("emp1", "EMPLOYEE"))
                .thenReturn(employee);
        when(tasks.findById(10)).thenReturn(Optional.of(task));
        when(timesheets.existsByEmployeeIdAndTaskIdAndWorkDate(7, 10, date))
                .thenReturn(true);

        assertThrows(
                BusinessRuleException.class,
                () -> service.submitTimesheet(
                        "emp1",
                        new SubmitTimesheetRequest(
                                10, date, new BigDecimal("8.00"), "Development")));
    }

    @Test
    void submitRejectsDailyTotalAboveTwentyFourHours() {
        LocalDate date = LocalDate.now();
        UserReference employee = mock(UserReference.class);
        when(employee.getUserId()).thenReturn(7);
        Task task = task(10, 7);
        when(userAccess.requireCurrentUser("emp1", "EMPLOYEE"))
                .thenReturn(employee);
        when(tasks.findById(10)).thenReturn(Optional.of(task));
        when(timesheets.existsByEmployeeIdAndTaskIdAndWorkDate(7, 10, date))
                .thenReturn(false);
        when(timesheets.sumHoursForEmployeeOnDate(7, date))
                .thenReturn(new BigDecimal("20.00"));

        assertThrows(
                BusinessRuleException.class,
                () -> service.submitTimesheet(
                        "emp1",
                        new SubmitTimesheetRequest(
                                10, date, new BigDecimal("5.00"), "Development")));
    }

    private Task task(Integer taskId, Integer employeeId) {
        Task task = new Task();
        task.setTaskId(taskId);
        task.setEmployeeId(employeeId);
        task.setStatus(TaskStatus.ACCEPTED);
        return task;
    }
}
