package com.tms.transactionservice.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import com.tms.transactionservice.dto.CreateTaskRequest;
import com.tms.transactionservice.dto.UpdateTaskProgressRequest;
import com.tms.transactionservice.dto.UpdateTaskRequest;
import com.tms.transactionservice.dto.response.TaskResponse;
import com.tms.transactionservice.entity.ProjectReference;
import com.tms.transactionservice.entity.Task;
import com.tms.transactionservice.entity.UserReference;
import com.tms.transactionservice.enums.TaskStatus;
import com.tms.transactionservice.exception.BusinessRuleException;
import com.tms.transactionservice.exception.ResourceNotFoundException;
import com.tms.transactionservice.repository.EmployeeProjectReferenceRepository;
import com.tms.transactionservice.repository.ProjectReferenceRepository;
import com.tms.transactionservice.repository.TaskRepository;
import com.tms.transactionservice.service.TaskService;
import com.tms.transactionservice.service.TransactionResponseMapper;
import com.tms.transactionservice.service.UserAccessService;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final UserAccessService userAccess;
    private final ProjectReferenceRepository projects;
    private final EmployeeProjectReferenceRepository employeeProjects;
    private final TaskRepository tasks;
    private final TransactionResponseMapper mapper;

    public TaskServiceImpl(
            UserAccessService userAccess,
            ProjectReferenceRepository projects,
            EmployeeProjectReferenceRepository employeeProjects,
            TaskRepository tasks,
            TransactionResponseMapper mapper) {
        this.userAccess = userAccess;
        this.projects = projects;
        this.employeeProjects = employeeProjects;
        this.tasks = tasks;
        this.mapper = mapper;
    }

    @Override
    public TaskResponse createTask(String username, CreateTaskRequest request) {
        UserReference manager = userAccess.requireCurrentUser(username, "MANAGER");
        ProjectReference project = requireManagedActiveProject(
                request.projectId(), manager.getUserId());
        requireAssignableEmployee(request.employeeId(), project.getProjectId());
        validateDates(request.startDate(), request.endDate());

        Task task = new Task();
        task.setProjectId(project.getProjectId());
        task.setManagerId(manager.getUserId());
        task.setEmployeeId(request.employeeId());
        task.setTaskName(request.taskName().trim());
        task.setTaskDescription(request.taskDescription());
        task.setStartDate(request.startDate());
        task.setEndDate(request.endDate());

        return mapper.toTaskResponse(tasks.save(task));
    }

    @Override
    public TaskResponse updateTask(
            String username,
            Integer taskId,
            UpdateTaskRequest request) {
        UserReference manager = userAccess.requireCurrentUser(username, "MANAGER");
        Task task = findTask(taskId);
        requireOwningManager(task, manager.getUserId());

        if (task.getStatus() == TaskStatus.COMPLETED) {
            throw new BusinessRuleException("A completed Task cannot be edited");
        }

        requireManagedActiveProject(task.getProjectId(), manager.getUserId());
        requireAssignableEmployee(request.employeeId(), task.getProjectId());
        validateDates(request.startDate(), request.endDate());

        task.setEmployeeId(request.employeeId());
        task.setTaskName(request.taskName().trim());
        task.setTaskDescription(request.taskDescription());
        task.setStartDate(request.startDate());
        task.setEndDate(request.endDate());

        return mapper.toTaskResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> myTasks(String username) {
        Integer employeeId = userAccess
                .requireCurrentUser(username, "EMPLOYEE")
                .getUserId();
        return tasks.findByEmployeeIdOrderByLastUpdatedDesc(employeeId)
                .stream()
                .map(mapper::toTaskResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> myManagedTasks(String username) {
        Integer managerId = userAccess
                .requireCurrentUser(username, "MANAGER")
                .getUserId();
        return tasks.findByManagerIdOrderByLastUpdatedDesc(managerId)
                .stream()
                .map(mapper::toTaskResponse)
                .toList();
    }

    @Override
    public TaskResponse acceptTask(String username, Integer taskId) {
        UserReference employee = userAccess.requireCurrentUser(username, "EMPLOYEE");
        Task task = findTask(taskId);
        requireTaskEmployee(task, employee.getUserId());

        if (task.getStatus() != TaskStatus.ASSIGNED) {
            throw new BusinessRuleException("Only an assigned Task can be accepted");
        }

        task.setStatus(TaskStatus.ACCEPTED);
        return mapper.toTaskResponse(task);
    }

    @Override
    public TaskResponse updateProgress(
            String username,
            Integer taskId,
            UpdateTaskProgressRequest request) {
        UserReference employee = userAccess.requireCurrentUser(username, "EMPLOYEE");
        Task task = findTask(taskId);
        requireTaskEmployee(task, employee.getUserId());

        if (task.getStatus() == TaskStatus.ASSIGNED) {
            throw new BusinessRuleException("Accept the Task before updating progress");
        }
        if (task.getStatus() == TaskStatus.COMPLETED) {
            throw new BusinessRuleException("A completed Task cannot be changed");
        }
        if (request.progressPercent() < task.getProgressPercent()) {
            throw new BusinessRuleException("Task progress cannot be reduced");
        }

        task.setProgressPercent(request.progressPercent());
        task.setRemarks(request.remarks());
        task.setStatus(request.progressPercent() == 100
                ? TaskStatus.COMPLETED
                : request.progressPercent() > 0
                    ? TaskStatus.IN_PROGRESS
                    : TaskStatus.ACCEPTED);

        return mapper.toTaskResponse(task);
    }

    private ProjectReference requireManagedActiveProject(
            Integer projectId,
            Integer managerId) {
        ProjectReference project = projects.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project not found: " + projectId));
        if (!managerId.equals(project.getManagerId())) {
            throw new AccessDeniedException(
                    "You can manage Tasks only for Projects assigned to you");
        }
        if (!"ACTIVE".equals(project.getStatus())) {
            throw new BusinessRuleException("Tasks require an active Project");
        }
        return project;
    }

    private void requireAssignableEmployee(Integer employeeId, Integer projectId) {
        userAccess.requireUser(employeeId, "EMPLOYEE");
        if (!employeeProjects.existsByEmployeeIdAndProjectId(employeeId, projectId)) {
            throw new BusinessRuleException("Employee is not assigned to this Project");
        }
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new BusinessRuleException("End date cannot be before start date");
        }
    }

    private Task findTask(Integer taskId) {
        return tasks.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found: " + taskId));
    }

    private void requireOwningManager(Task task, Integer managerId) {
        if (!managerId.equals(task.getManagerId())) {
            throw new AccessDeniedException("This Task belongs to another Manager");
        }
    }

    private void requireTaskEmployee(Task task, Integer employeeId) {
        if (!employeeId.equals(task.getEmployeeId())) {
            throw new AccessDeniedException("This Task belongs to another Employee");
        }
    }
}
