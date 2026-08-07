package com.tms.transactionservice.service;

import java.util.List;
import com.tms.transactionservice.dto.SubmitTimesheetRequest;
import com.tms.transactionservice.dto.response.TimesheetResponse;

/** Contract used by TimesheetController. */
public interface TimesheetService {
    TimesheetResponse submitTimesheet(String username, SubmitTimesheetRequest request);
    List<TimesheetResponse> myTimesheets(String username);
    List<TimesheetResponse> timesheetsForManager(String username);
}
