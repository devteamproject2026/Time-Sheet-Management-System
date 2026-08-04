package com.tms.transactionservice.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tms.transactionservice.dto.SubmitTimesheetRequest;
import com.tms.transactionservice.entity.Task;
import com.tms.transactionservice.entity.Timesheet;
import com.tms.transactionservice.entity.UserReference;
import com.tms.transactionservice.enums.TaskStatus;
import com.tms.transactionservice.exception.BusinessRuleException;
import com.tms.transactionservice.exception.ResourceNotFoundException;
import com.tms.transactionservice.repository.TaskRepository;
import com.tms.transactionservice.repository.TimesheetRepository;
import com.tms.transactionservice.repository.UserReferenceRepository;
import com.tms.transactionservice.service.TimesheetService;

/** Implements employee submission and history operations for timesheets. */
@Service
@Transactional
public class TimesheetServiceImpl implements TimesheetService {
	private final UserReferenceRepository users;
	private final TaskRepository tasks;
	private final TimesheetRepository timesheets;

	public TimesheetServiceImpl(UserReferenceRepository users, TaskRepository tasks, TimesheetRepository timesheets) {
		this.users = users;
		this.tasks = tasks;
		this.timesheets = timesheets;
	}

	@Override
	public Timesheet submitTimesheet(String username, SubmitTimesheetRequest request) {
		UserReference employee = currentUser(username);
		Task task = tasks.findById(request.taskId())
				.orElseThrow(() -> new ResourceNotFoundException("Task not found: " + request.taskId()));
		if (!task.getEmployeeId().equals(employee.getUserId()))
			throw new BusinessRuleException("You can submit time only for your own task");
		if (task.getStatus() == TaskStatus.ASSIGNED)
			throw new BusinessRuleException("Accept the task before submitting time");
		Timesheet timesheet = new Timesheet();
		timesheet.setEmployeeId(employee.getUserId());
		timesheet.setTaskId(task.getTaskId());
		timesheet.setWorkDate(request.workDate());
		timesheet.setHoursWorked(request.hoursWorked());
		timesheet.setWorkDescription(request.workDescription());
		return timesheets.save(timesheet);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Timesheet> myTimesheets(String username) {
		return timesheets.findByEmployeeIdOrderBySubmittedAtDesc(currentUser(username).getUserId());
	}

	@Override
	@Transactional(readOnly = true)
	public List<Timesheet> timesheetsForManager(String username) {
		Integer managerId = currentUser(username).getUserId();
		return timesheets.findAll().stream().filter(timesheet -> tasks.findById(timesheet.getTaskId())
				.map(task -> task.getManagerId().equals(managerId)).orElse(false)).toList();
	}

	private UserReference currentUser(String username) {
		return users.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("Logged-in user no longer exists"));
	}
}
