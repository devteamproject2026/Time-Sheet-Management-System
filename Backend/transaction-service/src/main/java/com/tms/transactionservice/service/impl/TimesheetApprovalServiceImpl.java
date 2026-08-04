package com.tms.transactionservice.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tms.transactionservice.dto.ReviewTimesheetRequest;
import com.tms.transactionservice.entity.Task;
import com.tms.transactionservice.entity.Timesheet;
import com.tms.transactionservice.entity.TimesheetApproval;
import com.tms.transactionservice.entity.UserReference;
import com.tms.transactionservice.enums.TimesheetStatus;
import com.tms.transactionservice.exception.BusinessRuleException;
import com.tms.transactionservice.exception.ResourceNotFoundException;
import com.tms.transactionservice.repository.TaskRepository;
import com.tms.transactionservice.repository.TimesheetApprovalRepository;
import com.tms.transactionservice.repository.TimesheetRepository;
import com.tms.transactionservice.repository.UserReferenceRepository;
import com.tms.transactionservice.service.TimesheetApprovalService;

/** Implements manager review and creates the timesheet-approval audit row. */
@Service
@Transactional
public class TimesheetApprovalServiceImpl implements TimesheetApprovalService {
	private final UserReferenceRepository users;
	private final TaskRepository tasks;
	private final TimesheetRepository timesheets;
	private final TimesheetApprovalRepository approvals;

	public TimesheetApprovalServiceImpl(UserReferenceRepository users, TaskRepository tasks,
			TimesheetRepository timesheets, TimesheetApprovalRepository approvals) {
		this.users = users;
		this.tasks = tasks;
		this.timesheets = timesheets;
		this.approvals = approvals;
	}

	@Override
	public Timesheet reviewTimesheet(String username, Integer timesheetId, ReviewTimesheetRequest request) {
		if (request.decision() == TimesheetStatus.PENDING)
			throw new BusinessRuleException("Decision must be APPROVED or REJECTED");
		Timesheet timesheet = timesheets.findById(timesheetId)
				.orElseThrow(() -> new ResourceNotFoundException("Timesheet not found: " + timesheetId));
		UserReference manager = users.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("Logged-in user no longer exists"));
		Task task = tasks.findById(timesheet.getTaskId())
				.orElseThrow(() -> new ResourceNotFoundException("Task not found for this timesheet"));
		if (!task.getManagerId().equals(manager.getUserId()))
			throw new BusinessRuleException("You do not manage this task");
		if (timesheet.getStatus() != TimesheetStatus.PENDING)
			throw new BusinessRuleException("This timesheet has already been reviewed");
		timesheet.setStatus(request.decision());
		TimesheetApproval approval = new TimesheetApproval();
		approval.setTimesheetId(timesheetId);
		approval.setManagerId(manager.getUserId());
		approval.setApprovalStatus(request.decision());
		approval.setComments(request.comments());
		approvals.save(approval);
		return timesheet;
	}
}
