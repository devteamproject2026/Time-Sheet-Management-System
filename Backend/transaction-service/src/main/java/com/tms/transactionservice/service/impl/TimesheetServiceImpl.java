package com.tms.transactionservice.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import com.tms.transactionservice.dto.SubmitTimesheetRequest;
import com.tms.transactionservice.dto.response.TimesheetResponse;
import com.tms.transactionservice.entity.Task;
import com.tms.transactionservice.entity.Timesheet;
import com.tms.transactionservice.entity.UserReference;
import com.tms.transactionservice.enums.TaskStatus;
import com.tms.transactionservice.exception.BusinessRuleException;
import com.tms.transactionservice.exception.ResourceNotFoundException;
import com.tms.transactionservice.repository.TaskRepository;
import com.tms.transactionservice.repository.TimesheetRepository;
import com.tms.transactionservice.service.TimesheetService;
import com.tms.transactionservice.service.TransactionResponseMapper;
import com.tms.transactionservice.service.UserAccessService;

@Service
@Transactional
public class TimesheetServiceImpl implements TimesheetService {

    private static final BigDecimal MAX_DAILY_HOURS = new BigDecimal("24.00");

    private final UserAccessService userAccess;
    private final TaskRepository tasks;
    private final TimesheetRepository timesheets;
    private final TransactionResponseMapper mapper;

    public TimesheetServiceImpl(
            UserAccessService userAccess,
            TaskRepository tasks,
            TimesheetRepository timesheets,
            TransactionResponseMapper mapper) {
        this.userAccess = userAccess;
        this.tasks = tasks;
        this.timesheets = timesheets;
        this.mapper = mapper;
    }

    @Override
    public TimesheetResponse submitTimesheet(
            String username,
            SubmitTimesheetRequest request) {
        UserReference employee = userAccess.requireCurrentUser(username, "EMPLOYEE");
        Task task = tasks.findById(request.taskId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found: " + request.taskId()));

        if (!employee.getUserId().equals(task.getEmployeeId())) {
            throw new AccessDeniedException(
                    "You can submit time only for your own Task");
        }
        if (task.getStatus() == TaskStatus.ASSIGNED) {
            throw new BusinessRuleException("Accept the Task before submitting time");
        }
        if (task.getStartDate() != null
                && request.workDate().isBefore(task.getStartDate())) {
            throw new BusinessRuleException("Work date cannot be before Task start date");
        }
        if (task.getEndDate() != null
                && request.workDate().isAfter(task.getEndDate())) {
            throw new BusinessRuleException("Work date cannot be after Task end date");
        }
        if (timesheets.existsByEmployeeIdAndTaskIdAndWorkDate(
                employee.getUserId(), task.getTaskId(), request.workDate())) {
            throw new BusinessRuleException(
                    "A Timesheet already exists for this Task and work date");
        }

        BigDecimal existingHours = timesheets.sumHoursForEmployeeOnDate(
                employee.getUserId(), request.workDate());
        if (existingHours.add(request.hoursWorked()).compareTo(MAX_DAILY_HOURS) > 0) {
            throw new BusinessRuleException(
                    "Total Timesheet hours cannot exceed 24 hours per day");
        }

        Timesheet timesheet = new Timesheet();
        timesheet.setEmployeeId(employee.getUserId());
        timesheet.setTaskId(task.getTaskId());
        timesheet.setWorkDate(request.workDate());
        timesheet.setHoursWorked(request.hoursWorked());
        timesheet.setWorkDescription(request.workDescription().trim());

        return mapper.toTimesheetResponse(timesheets.save(timesheet));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimesheetResponse> myTimesheets(String username) {
        Integer employeeId = userAccess
                .requireCurrentUser(username, "EMPLOYEE")
                .getUserId();
        return timesheets.findByEmployeeIdOrderBySubmittedAtDesc(employeeId)
                .stream()
                .map(mapper::toTimesheetResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimesheetResponse> timesheetsForManager(String username) {
        Integer managerId = userAccess
                .requireCurrentUser(username, "MANAGER")
                .getUserId();
        return timesheets.findAllForManager(managerId)
                .stream()
                .map(mapper::toTimesheetResponse)
                .toList();
    }
}
