package com.tms.transactionservice.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.tms.transactionservice.enums.AttendanceStatus;

public record AttendanceResponse(
        Integer attendanceId,
        Integer employeeId,
        String employeeUsername,
        String employeeFullName,
        LocalDate attendanceDate,
        LocalTime checkIn,
        LocalTime checkOut,
        AttendanceStatus status) {}
