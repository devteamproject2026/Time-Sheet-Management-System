package com.tms.transactionservice.service;

import java.time.LocalDate;
import java.util.List;

import com.tms.transactionservice.dto.response.AttendanceResponse;

public interface AttendanceService {
    AttendanceResponse checkIn(String username);
    AttendanceResponse checkOut(String username);
    List<AttendanceResponse> myAttendance(String username);
    List<AttendanceResponse> teamAttendance(String username, LocalDate date);
}
