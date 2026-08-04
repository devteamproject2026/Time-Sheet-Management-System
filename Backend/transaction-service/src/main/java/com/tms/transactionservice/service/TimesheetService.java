package com.tms.transactionservice.service;

import java.util.List;
import com.tms.transactionservice.dto.SubmitTimesheetRequest;
import com.tms.transactionservice.entity.Timesheet;

/** Contract used by TimesheetController. */
public interface TimesheetService {
    Timesheet submitTimesheet(String username, SubmitTimesheetRequest request);
    List<Timesheet> myTimesheets(String username);
    List<Timesheet> timesheetsForManager(String username);
}
