package com.tms.authservice.service;

import java.util.List;

import com.tms.authservice.dto.LoginRequest;
import com.tms.authservice.dto.LoginResponse;
import com.tms.authservice.dto.RegisterHrRequest;
import com.tms.authservice.entity.User;
import com.tms.authservice.entity.enums.AccountStatus;
import com.tms.authservice.entity.enums.ApprovalStatus;
import com.tms.authservice.entity.enums.Role;

public interface AuthService {

    // Generic User Registration
    String registerUser(RegisterHrRequest request,
                        Role role,
                        ApprovalStatus approvalStatus,
                        AccountStatus accountStatus);

    // Login
    LoginResponse login(LoginRequest request);

    // Pending HR Requests
    List<User> getPendingHrRequests();

    // Approve HR
    String approveHr(Integer userId);

    // Reject HR
    String rejectHr(Integer userId);

}