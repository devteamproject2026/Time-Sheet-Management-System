package com.tms.transactionservice.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import com.tms.transactionservice.dto.response.EmployeeReportResponse;
import com.tms.transactionservice.entity.Task;
import com.tms.transactionservice.entity.Timesheet;
import com.tms.transactionservice.entity.UserReference;
import com.tms.transactionservice.enums.TaskStatus;
import com.tms.transactionservice.enums.TimesheetStatus;
import com.tms.transactionservice.exception.ResourceNotFoundException;
import com.tms.transactionservice.repository.EmployeeProjectReferenceRepository;
import com.tms.transactionservice.repository.TaskRepository;
import com.tms.transactionservice.repository.TimesheetRepository;
import com.tms.transactionservice.repository.UserReferenceRepository;
import com.tms.transactionservice.service.EmployeeReportService;
import com.tms.transactionservice.service.UserAccessService;

@Service
@Transactional(readOnly = true)
public class EmployeeReportServiceImpl implements EmployeeReportService {

    private final UserAccessService userAccess;
    private final UserReferenceRepository users;
    private final EmployeeProjectReferenceRepository employeeProjects;
    private final TaskRepository tasks;
    private final TimesheetRepository timesheets;

    public EmployeeReportServiceImpl(
            UserAccessService userAccess,
            UserReferenceRepository users,
            EmployeeProjectReferenceRepository employeeProjects,
            TaskRepository tasks,
            TimesheetRepository timesheets) {
        this.userAccess = userAccess;
        this.users = users;
        this.employeeProjects = employeeProjects;
        this.tasks = tasks;
        this.timesheets = timesheets;
    }

    @Override
    public List<EmployeeReportResponse> myEmployeeReports(String username) {
        Integer managerId = userAccess
                .requireCurrentUser(username, "MANAGER")
                .getUserId();
        return employeeProjects.findEmployeeIdsManagedBy(managerId)
                .stream()
                .map(employeeId -> buildReport(managerId, employeeId))
                .toList();
    }

    @Override
    public EmployeeReportResponse myEmployeeReport(
            String username,
            Integer employeeId) {
        Integer managerId = userAccess
                .requireCurrentUser(username, "MANAGER")
                .getUserId();
        if (!employeeProjects.findEmployeeIdsManagedBy(managerId).contains(employeeId)) {
            // A Manager may see reports only for Employees in their Projects.
            throw new AccessDeniedException(
                    "This Employee is not assigned to one of your Projects");
        }
        return buildReport(managerId, employeeId);
    }

    private EmployeeReportResponse buildReport(Integer managerId, Integer employeeId) {
        UserReference employee = users.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found: " + employeeId));
        List<Task> employeeTasks = tasks
                .findByManagerIdAndEmployeeIdOrderByLastUpdatedDesc(
                        managerId, employeeId);
        List<Integer> taskIds = employeeTasks.stream().map(Task::getTaskId).toList();
        List<Timesheet> employeeTimesheets = taskIds.isEmpty()
                ? List.of()
                : timesheets.findByTaskIdIn(taskIds);

        long completedTasks = employeeTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
                .count();
        double averageProgress = employeeTasks.stream()
                .mapToInt(Task::getProgressPercent)
                .average()
                .orElse(0.0);
        BigDecimal approvedHours = employeeTimesheets.stream()
                .filter(item -> item.getStatus() == TimesheetStatus.APPROVED)
                .map(Timesheet::getHoursWorked)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        return new EmployeeReportResponse(
                employee.getUserId(),
                employee.getUsername(),
                (employee.getFirstName() + " " + employee.getLastName()).trim(),
                employeeTasks.size(),
                completedTasks,
                Math.round(averageProgress * 100.0) / 100.0,
                approvedHours,
                countTimesheets(employeeTimesheets, TimesheetStatus.PENDING),
                countTimesheets(employeeTimesheets, TimesheetStatus.APPROVED),
                countTimesheets(employeeTimesheets, TimesheetStatus.REJECTED));
    }

    private long countTimesheets(
            List<Timesheet> employeeTimesheets,
            TimesheetStatus status) {
        return employeeTimesheets.stream()
                .filter(item -> item.getStatus() == status)
                .count();
    }
}
