package com.tms.transactionservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import com.tms.transactionservice.entity.UserReference;
import com.tms.transactionservice.exception.BusinessRuleException;
import com.tms.transactionservice.exception.ResourceNotFoundException;
import com.tms.transactionservice.repository.UserReferenceRepository;

/** Central validation for users referenced from Auth Service-owned data. */
@Service
@Transactional(readOnly = true)
public class UserAccessService {

    private final UserReferenceRepository users;

    public UserAccessService(UserReferenceRepository users) {
        this.users = users;
    }

    public UserReference requireCurrentUser(String username, String expectedRole) {
        UserReference user = users.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Logged-in user no longer exists"));

        // The JWT may be valid while the database account has since been
        // disabled. That is an authorization failure, so return HTTP 403.
        if (!expectedRole.equals(user.getRole())) {
            throw new AccessDeniedException(
                    "Your account cannot perform this operation");
        }
        if (!"APPROVED".equals(user.getApprovalStatus())
                || !"ACTIVE".equals(user.getAccountStatus())) {
            throw new AccessDeniedException(
                    "User account must be approved and active");
        }
        return user;
    }

    public UserReference requireUser(Integer userId, String expectedRole) {
        UserReference user = users.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        expectedRole + " not found: " + userId));
        return requireUsableRole(user, expectedRole);
    }

    private UserReference requireUsableRole(
            UserReference user,
            String expectedRole) {

        if (!expectedRole.equals(user.getRole())) {
            throw new BusinessRuleException(
                    "Selected user must have role " + expectedRole);
        }

        if (!"APPROVED".equals(user.getApprovalStatus())
                || !"ACTIVE".equals(user.getAccountStatus())) {
            throw new BusinessRuleException(
                    "User account must be approved and active");
        }

        return user;
    }
}
