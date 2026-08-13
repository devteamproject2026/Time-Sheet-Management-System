package com.tms.transactionservice.service;

import java.util.List;
import com.tms.transactionservice.dto.CreateTaskRequest;
import com.tms.transactionservice.dto.UpdateTaskRequest;
import com.tms.transactionservice.dto.UpdateTaskProgressRequest;
import com.tms.transactionservice.dto.response.TaskResponse;

/** Contract used by TaskController; implementation details stay in service.impl. */
public interface TaskService {
    TaskResponse createTask(String username, CreateTaskRequest request);
    TaskResponse updateTask(String username, Integer taskId, UpdateTaskRequest request);
    List<TaskResponse> myTasks(String username);
    List<TaskResponse> myManagedTasks(String username);
    TaskResponse acceptTask(String username, Integer taskId);
    TaskResponse updateProgress(String username, Integer taskId, UpdateTaskProgressRequest request);
}
