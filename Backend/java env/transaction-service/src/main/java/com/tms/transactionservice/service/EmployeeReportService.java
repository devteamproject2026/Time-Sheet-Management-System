package com.tms.transactionservice.service;

import java.util.List;

import com.tms.transactionservice.dto.response.EmployeeReportResponse;

public interface EmployeeReportService {
    List<EmployeeReportResponse> myEmployeeReports(String username);
    EmployeeReportResponse myEmployeeReport(String username, Integer employeeId);
}
