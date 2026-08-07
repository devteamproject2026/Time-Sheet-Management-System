package com.tms.transactionservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tms.transactionservice.dto.UpdateTaskProgressRequest;
import com.tms.transactionservice.entity.Task;
import com.tms.transactionservice.entity.UserReference;
import com.tms.transactionservice.enums.TaskStatus;
import com.tms.transactionservice.exception.BusinessRuleException;
import com.tms.transactionservice.repository.EmployeeProjectReferenceRepository;
import com.tms.transactionservice.repository.ProjectReferenceRepository;
import com.tms.transactionservice.repository.TaskRepository;
import com.tms.transactionservice.service.TransactionResponseMapper;
import com.tms.transactionservice.service.UserAccessService;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock UserAccessService userAccess;
    @Mock ProjectReferenceRepository projects;
    @Mock EmployeeProjectReferenceRepository employeeProjects;
    @Mock TaskRepository tasks;
    @Mock TransactionResponseMapper mapper;

    private TaskServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TaskServiceImpl(
                userAccess, projects, employeeProjects, tasks, mapper);
    }

    @Test
    void updateProgressRequiresTaskAcceptance() {
        UserReference employee = mock(UserReference.class);
        when(employee.getUserId()).thenReturn(7);
        Task task = task(10, 7, TaskStatus.ASSIGNED, 0);
        when(userAccess.requireCurrentUser("emp1", "EMPLOYEE"))
                .thenReturn(employee);
        when(tasks.findById(10)).thenReturn(Optional.of(task));

        assertThrows(
                BusinessRuleException.class,
                () -> service.updateProgress(
                        "emp1", 10, new UpdateTaskProgressRequest(25, "Started")));
    }

    @Test
    void updateProgressCannotMoveBackwards() {
        UserReference employee = mock(UserReference.class);
        when(employee.getUserId()).thenReturn(7);
        Task task = task(10, 7, TaskStatus.IN_PROGRESS, 60);
        when(userAccess.requireCurrentUser("emp1", "EMPLOYEE"))
                .thenReturn(employee);
        when(tasks.findById(10)).thenReturn(Optional.of(task));

        assertThrows(
                BusinessRuleException.class,
                () -> service.updateProgress(
                        "emp1", 10, new UpdateTaskProgressRequest(40, "Incorrect")));
    }

    private Task task(
            Integer taskId,
            Integer employeeId,
            TaskStatus status,
            Integer progress) {
        Task task = new Task();
        task.setTaskId(taskId);
        task.setEmployeeId(employeeId);
        task.setStatus(status);
        task.setProgressPercent(progress);
        return task;
    }
}
