package com.tms.authservice.service;

import java.util.List;

import com.tms.authservice.dto.ChangePasswordRequest;
import com.tms.authservice.dto.CurrentUserResponse;
import com.tms.authservice.dto.LoginRequest;
import com.tms.authservice.dto.RegisterHrRequest;
import com.tms.authservice.dto.UserLookupResponse;
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
    AuthenticatedLogin login(LoginRequest request);

    // Restore the currently authenticated user's safe profile after a page refresh
    CurrentUserResponse getCurrentUser(String username);

    // Safe lookup lists used by HR while creating Projects and assignments
    List<UserLookupResponse> getActiveManagers();

    List<UserLookupResponse> getActiveEmployees();

    // Pending HR Requests
    List<User> getPendingHrRequests();

    // Approve HR
    String approveHr(Integer userId);

    // Reject HR
    String rejectHr(Integer userId);
    
    // Reset Password
    void changePassword(ChangePasswordRequest request);

}
