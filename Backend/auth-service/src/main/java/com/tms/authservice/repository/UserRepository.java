package com.tms.authservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.authservice.entity.User;
import com.tms.authservice.entity.enums.AccountStatus;
import com.tms.authservice.entity.enums.ApprovalStatus;
import com.tms.authservice.entity.enums.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Find user by username
    Optional<User> findByUsername(String username);

    // Login (Only approved & active users)
    Optional<User> findByUsernameAndApprovalStatusAndAccountStatus(
            String username,
            ApprovalStatus approvalStatus,
            AccountStatus accountStatus);

    // Check duplicate email
    boolean existsByEmail(String email);

    // Check duplicate username
    boolean existsByUsername(String username);

    // Pending HR Requests
    List<User> findByRoleAndApprovalStatus(
            Role role,
            ApprovalStatus approvalStatus);

}