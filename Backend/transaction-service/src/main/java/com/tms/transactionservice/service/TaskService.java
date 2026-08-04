package com.tms.transactionservice.service;

import java.util.List;
import com.tms.transactionservice.dto.CreateTaskRequest;
import com.tms.transactionservice.dto.UpdateTaskProgressRequest;
import com.tms.transactionservice.entity.Task;

/** Contract used by TaskController; implementation details stay in service.impl. */
public interface TaskService {
    Task createTask(String username, CreateTaskRequest request);
    List<Task> myTasks(String username);
    List<Task> myManagedTasks(String username);
    Task acceptTask(String username, Integer taskId);
    Task updateProgress(String username, Integer taskId, UpdateTaskProgressRequest request);
}
