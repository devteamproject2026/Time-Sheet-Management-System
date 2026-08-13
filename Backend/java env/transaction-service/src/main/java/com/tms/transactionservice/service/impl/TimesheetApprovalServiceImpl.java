package com.tms.transactionservice.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import com.tms.transactionservice.dto.ReviewTimesheetRequest;
import com.tms.transactionservice.dto.response.TimesheetResponse;
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
import com.tms.transactionservice.service.TimesheetApprovalService;
import com.tms.transactionservice.service.TransactionResponseMapper;
import com.tms.transactionservice.service.UserAccessService;

@Service
@Transactional
public class TimesheetApprovalServiceImpl implements TimesheetApprovalService {

    private final UserAccessService userAccess;
    private final TaskRepository tasks;
    private final TimesheetRepository timesheets;
    private final TimesheetApprovalRepository approvals;
    private final TransactionResponseMapper mapper;

    public TimesheetApprovalServiceImpl(
            UserAccessService userAccess,
            TaskRepository tasks,
            TimesheetRepository timesheets,
            TimesheetApprovalRepository approvals,
            TransactionResponseMapper mapper) {
        this.userAccess = userAccess;
        this.tasks = tasks;
        this.timesheets = timesheets;
        this.approvals = approvals;
        this.mapper = mapper;
    }

    @Override
    public TimesheetResponse reviewTimesheet(
            String username,
            Integer timesheetId,
            ReviewTimesheetRequest request) {
        if (request.decision() == TimesheetStatus.PENDING) {
            throw new BusinessRuleException("Decision must be APPROVED or REJECTED");
        }

        UserReference manager = userAccess.requireCurrentUser(username, "MANAGER");
        Timesheet timesheet = timesheets.findById(timesheetId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Timesheet not found: " + timesheetId));
        Task task = tasks.findById(timesheet.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found for this Timesheet"));

        if (!manager.getUserId().equals(task.getManagerId())) {
            throw new AccessDeniedException("You do not manage this Task");
        }
        if (timesheet.getStatus() != TimesheetStatus.PENDING) {
            throw new BusinessRuleException("This Timesheet has already been reviewed");
        }

        timesheet.setStatus(request.decision());

        TimesheetApproval approval = new TimesheetApproval();
        approval.setTimesheetId(timesheetId);
        approval.setManagerId(manager.getUserId());
        approval.setApprovalStatus(request.decision());
        approval.setComments(request.comments());
        approvals.save(approval);

        return mapper.toTimesheetResponse(timesheet);
    }
}
