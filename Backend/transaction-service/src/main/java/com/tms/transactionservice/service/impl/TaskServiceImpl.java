package com.tms.transactionservice.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tms.transactionservice.dto.CreateTaskRequest;
import com.tms.transactionservice.dto.UpdateTaskProgressRequest;
import com.tms.transactionservice.entity.ProjectReference;
import com.tms.transactionservice.entity.Task;
import com.tms.transactionservice.entity.UserReference;
import com.tms.transactionservice.enums.TaskStatus;
import com.tms.transactionservice.exception.BusinessRuleException;
import com.tms.transactionservice.exception.ResourceNotFoundException;
import com.tms.transactionservice.repository.EmployeeProjectReferenceRepository;
import com.tms.transactionservice.repository.ProjectReferenceRepository;
import com.tms.transactionservice.repository.TaskRepository;
import com.tms.transactionservice.repository.UserReferenceRepository;
import com.tms.transactionservice.service.TaskService;

/** Implements task-table rules and validates the Business Service references. */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final UserReferenceRepository users;
    private final ProjectReferenceRepository projects;
    private final EmployeeProjectReferenceRepository employeeProjects;
    private final TaskRepository tasks;

    public TaskServiceImpl(
            UserReferenceRepository users,
            ProjectReferenceRepository projects,
            EmployeeProjectReferenceRepository employeeProjects,
            TaskRepository tasks) {

        this.users = users;
        this.projects = projects;
        this.employeeProjects = employeeProjects;
        this.tasks = tasks;
    }

    @Override
    public Task createTask(String username, CreateTaskRequest request) {

        UserReference manager = currentUser(username);
        ProjectReference project = projects.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!manager.getUserId().equals(project.getManagerId())) {
            throw new BusinessRuleException(
                    "You can create tasks only for projects you manage");
        }

        if (!employeeProjects.existsByEmployeeIdAndProjectId(
                request.employeeId(), request.projectId())) {
            throw new BusinessRuleException(
                    "Employee is not assigned to this project");
        }

        if (request.startDate() != null
                && request.endDate() != null
                && request.endDate().isBefore(request.startDate())) {
            throw new BusinessRuleException(
                    "End date cannot be before start date");
        }

        Task task = new Task();
        task.setProjectId(request.projectId());
        task.setManagerId(manager.getUserId());
        task.setEmployeeId(request.employeeId());
        task.setTaskName(request.taskName());
        task.setTaskDescription(request.taskDescription());
        task.setStartDate(request.startDate());
        task.setEndDate(request.endDate());

        return tasks.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> myTasks(String username) {
        return tasks.findByEmployeeIdOrderByLastUpdatedDesc(
                currentUser(username).getUserId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> myManagedTasks(String username) {
        return tasks.findByManagerIdOrderByLastUpdatedDesc(
                currentUser(username).getUserId());
    }

    @Override
    public Task acceptTask(String username, Integer taskId) {

        Task task = findTask(taskId);
        requireTaskEmployee(task, username);

        if (task.getStatus() != TaskStatus.ASSIGNED) {
            throw new BusinessRuleException(
                    "Only an assigned task can be accepted");
        }

        task.setStatus(TaskStatus.ACCEPTED);
        return task;
    }

    @Override
    public Task updateProgress(
            String username,
            Integer taskId,
            UpdateTaskProgressRequest request) {

        Task task = findTask(taskId);
        requireTaskEmployee(task, username);

        task.setProgressPercent(request.progressPercent());
        task.setRemarks(request.remarks());
        task.setStatus(request.progressPercent() == 100
                ? TaskStatus.COMPLETED
                : TaskStatus.IN_PROGRESS);

        return task;
    }

    private UserReference currentUser(String username) {
        return users.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Logged-in user no longer exists"));
    }

    private Task findTask(Integer taskId) {
        return tasks.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found: " + taskId));
    }

    private void requireTaskEmployee(Task task, String username) {
        if (!task.getEmployeeId().equals(currentUser(username).getUserId())) {
            throw new BusinessRuleException(
                    "This task belongs to another employee");
        }
    }
}
