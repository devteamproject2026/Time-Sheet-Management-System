package com.tms.transactionservice.service;

import com.tms.transactionservice.dto.ReviewTimesheetRequest;
import com.tms.transactionservice.entity.Timesheet;

/** Contract used by TimesheetApprovalController. */
public interface TimesheetApprovalService {
    Timesheet reviewTimesheet(String username, Integer timesheetId, ReviewTimesheetRequest request);
}
