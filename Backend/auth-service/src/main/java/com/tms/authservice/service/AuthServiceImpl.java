package com.tms.authservice.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.tms.authservice.dto.LoginRequest;
import com.tms.authservice.dto.LoginResponse;
import com.tms.authservice.dto.RegisterHrRequest;
import com.tms.authservice.entity.User;
import com.tms.authservice.entity.enums.AccountStatus;
import com.tms.authservice.entity.enums.ApprovalStatus;
import com.tms.authservice.entity.enums.Role;
import com.tms.authservice.repository.UserRepository;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //=============================================
    //          GENERIC USER REGISTRATION
    //=============================================
    @Override
    public String registerUser(RegisterHrRequest request,
                               Role role,
                               ApprovalStatus approvalStatus,
                               AccountStatus accountStatus) {

        // Check duplicate username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Check duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .contact(request.getContact())
                .joiningDate(request.getJoiningDate())
                .role(role)
                .approvalStatus(approvalStatus)
                .accountStatus(accountStatus)
                .build();

        userRepository.save(user);

        return role + " Registered Successfully";
    }

    //=============================================
    //                LOGIN
    //=============================================
    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository
                .findByUsernameAndApprovalStatusAndAccountStatus(
                        request.getUsername(),
                        ApprovalStatus.APPROVED,
                        AccountStatus.ACTIVE)
                .orElseThrow(() ->
                        new RuntimeException("Invalid Username or Account Not Active"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid Password");
        }

        return LoginResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .token("abc123") // Replace with JWT later
                .message("Login Successful")
                .build();
    }

    //=============================================
    //          GET ALL PENDING HR
    //=============================================
    @Override
    public List<User> getPendingHrRequests() {

        return userRepository.findByRoleAndApprovalStatus(
                Role.HR_HEAD,
                ApprovalStatus.PENDING);
    }

    //=============================================
    //             APPROVE HR
    //=============================================
    @Override
    public String approveHr(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("HR Not Found"));

        user.setApprovalStatus(ApprovalStatus.APPROVED);
        user.setAccountStatus(AccountStatus.ACTIVE);

        userRepository.save(user);

        return "HR Approved Successfully";
    }

    //=============================================
    //             REJECT HR
    //=============================================
    @Override
    public String rejectHr(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("HR Not Found"));

        user.setApprovalStatus(ApprovalStatus.REJECTED);
        user.setAccountStatus(AccountStatus.INACTIVE);

        userRepository.save(user);

        return "HR Rejected Successfully";
    }

}