package com.tms.authservice.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.tms.authservice.dto.ChangePasswordRequest;
import com.tms.authservice.dto.CurrentUserResponse;
import com.tms.authservice.dto.LoginRequest;
import com.tms.authservice.dto.LoginResponse;
import com.tms.authservice.dto.RegisterHrRequest;
import com.tms.authservice.dto.UserLookupResponse;
import com.tms.authservice.entity.User;
import com.tms.authservice.entity.enums.AccountStatus;
import com.tms.authservice.entity.enums.ApprovalStatus;
import com.tms.authservice.entity.enums.Role;
import com.tms.authservice.repository.UserRepository;

import com.tms.authservice.security.JwtService;

@Service
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder,
            JwtService jwtService) {

this.userRepository = userRepository;
this.passwordEncoder = passwordEncoder;
this.jwtService = jwtService;
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
    public AuthenticatedLogin login(LoginRequest request) {

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

        // Generate JWT Token
        String token = jwtService.generateToken(user);

        LoginResponse response = LoginResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .message("Login Successful")
                .build();

        // The controller places the token only in an HttpOnly cookie.
        return new AuthenticatedLogin(response, token);
    }

    //=============================================
    //       RESTORE AUTHENTICATED USER SESSION
    //=============================================
    @Override
    public CurrentUserResponse getCurrentUser(String username) {

        // Re-check the database so an inactive or unapproved account is not
        // restored only because an older JWT cookie is still present.
        User user = userRepository
                .findByUsernameAndApprovalStatusAndAccountStatus(
                        username,
                        ApprovalStatus.APPROVED,
                        AccountStatus.ACTIVE)
                .orElseThrow(() ->
                        new RuntimeException("User account is not active"));

        // Return only the information required by Redux and route authorization.
        return CurrentUserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    //=============================================
    //       ACTIVE MANAGER/EMPLOYEE LOOKUPS
    //=============================================
    @Override
    public List<UserLookupResponse> getActiveManagers() {

        return getActiveUsersByRole(Role.MANAGER);
    }

    @Override
    public List<UserLookupResponse> getActiveEmployees() {

        return getActiveUsersByRole(Role.EMPLOYEE);
    }

    /**
     * Returns only approved and active users. This prevents HR from assigning
     * an inactive account to a Project.
     */
    private List<UserLookupResponse> getActiveUsersByRole(Role role) {

        return userRepository
                .findByRoleAndApprovalStatusAndAccountStatusOrderByFirstNameAscLastNameAsc(
                        role,
                        ApprovalStatus.APPROVED,
                        AccountStatus.ACTIVE)
                .stream()
                .map(this::toLookupResponse)
                .toList();
    }

    /**
     * Converts the User entity to a safe DTO. The password is never returned.
     */
    private UserLookupResponse toLookupResponse(User user) {

        String fullName =
                (user.getFirstName() + " " + user.getLastName()).trim();

        return UserLookupResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .fullName(fullName)
                .email(user.getEmail())
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
    
    
  //=============================================
  //             REST PASSWORD 
  //=============================================
    @Override
    public void changePassword(ChangePasswordRequest request) {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // Verify current password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        // Verify new password and confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match.");
        }

        // Prevent using the same password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from the current password.");
        }

        // Encode and save new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }

}
