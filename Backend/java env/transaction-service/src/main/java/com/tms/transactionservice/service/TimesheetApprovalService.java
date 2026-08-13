package com.tms.transactionservice.service;

import com.tms.transactionservice.dto.ReviewTimesheetRequest;
import com.tms.transactionservice.dto.response.TimesheetResponse;

/** Contract used by TimesheetApprovalController. */
public interface TimesheetApprovalService {
    TimesheetResponse reviewTimesheet(String username, Integer timesheetId, ReviewTimesheetRequest request);
}
