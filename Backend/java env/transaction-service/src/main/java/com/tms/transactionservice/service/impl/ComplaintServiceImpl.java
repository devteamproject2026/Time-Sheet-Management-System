package com.tms.transactionservice.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import com.tms.transactionservice.dto.CreateComplaintRequest;
import com.tms.transactionservice.dto.ResolveComplaintRequest;
import com.tms.transactionservice.dto.response.ComplaintResponse;
import com.tms.transactionservice.dto.response.UserSummaryResponse;
import com.tms.transactionservice.entity.Complaint;
import com.tms.transactionservice.entity.UserReference;
import com.tms.transactionservice.enums.ComplaintStatus;
import com.tms.transactionservice.exception.BusinessRuleException;
import com.tms.transactionservice.exception.ResourceNotFoundException;
import com.tms.transactionservice.repository.ComplaintRepository;
import com.tms.transactionservice.repository.EmployeeProjectReferenceRepository;
import com.tms.transactionservice.repository.UserReferenceRepository;
import com.tms.transactionservice.service.ComplaintService;
import com.tms.transactionservice.service.TransactionResponseMapper;
import com.tms.transactionservice.service.UserAccessService;

@Service
@Transactional
public class ComplaintServiceImpl implements ComplaintService {

    private final UserAccessService userAccess;
    private final UserReferenceRepository users;
    private final EmployeeProjectReferenceRepository employeeProjects;
    private final ComplaintRepository complaints;
    private final TransactionResponseMapper mapper;

    public ComplaintServiceImpl(
            UserAccessService userAccess,
            UserReferenceRepository users,
            EmployeeProjectReferenceRepository employeeProjects,
            ComplaintRepository complaints,
            TransactionResponseMapper mapper) {
        this.userAccess = userAccess;
        this.users = users;
        this.employeeProjects = employeeProjects;
        this.complaints = complaints;
        this.mapper = mapper;
    }

    @Override
    public ComplaintResponse raiseComplaint(
            String username,
            CreateComplaintRequest request) {
        UserReference employee = userAccess.requireCurrentUser(username, "EMPLOYEE");
        UserReference manager = userAccess.requireUser(request.managerId(), "MANAGER");

        if (employeeProjects.countAssignmentsWithManager(
                employee.getUserId(), manager.getUserId()) == 0) {
            // Prevent Employees from selecting an unrelated Manager ID.
            throw new AccessDeniedException(
                    "You can raise a Complaint only to a Manager of your assigned Projects");
        }

        Complaint complaint = new Complaint();
        complaint.setEmployeeId(employee.getUserId());
        complaint.setManagerId(manager.getUserId());
        complaint.setSubject(request.subject().trim());
        complaint.setDescription(request.description().trim());

        return mapper.toComplaintResponse(complaints.save(complaint));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintResponse> myComplaints(String username) {
        Integer employeeId = userAccess
                .requireCurrentUser(username, "EMPLOYEE")
                .getUserId();
        return complaints.findByEmployeeIdOrderByCreatedAtDesc(employeeId)
                .stream()
                .map(mapper::toComplaintResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintResponse> assignedComplaints(String username) {
        Integer managerId = userAccess
                .requireCurrentUser(username, "MANAGER")
                .getUserId();
        return complaints.findByManagerIdOrderByCreatedAtDesc(managerId)
                .stream()
                .map(mapper::toComplaintResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryResponse> availableManagers(String username) {
        Integer employeeId = userAccess
                .requireCurrentUser(username, "EMPLOYEE")
                .getUserId();
        return employeeProjects.findManagerIdsForEmployee(employeeId)
                .stream()
                .map(users::findById)
                .flatMap(java.util.Optional::stream)
                .filter(user -> "MANAGER".equals(user.getRole()))
                .filter(user -> "ACTIVE".equals(user.getAccountStatus()))
                .filter(user -> "APPROVED".equals(user.getApprovalStatus()))
                .map(mapper::toUserSummary)
                .toList();
    }

    @Override
    public ComplaintResponse resolveComplaint(
            String username,
            Integer complaintId,
            ResolveComplaintRequest request) {
        UserReference manager = userAccess.requireCurrentUser(username, "MANAGER");
        Complaint complaint = complaints.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Complaint not found: " + complaintId));

        if (!manager.getUserId().equals(complaint.getManagerId())) {
            throw new AccessDeniedException(
                    "This Complaint belongs to another Manager");
        }
        if (complaint.getStatus() == ComplaintStatus.RESOLVED) {
            throw new BusinessRuleException("This Complaint is already resolved");
        }

        complaint.setResolution(request.resolution().trim());
        complaint.setStatus(ComplaintStatus.RESOLVED);
        complaint.setResolvedAt(LocalDateTime.now());

        return mapper.toComplaintResponse(complaint);
    }
}
