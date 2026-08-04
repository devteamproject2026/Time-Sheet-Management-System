package com.tms.transactionservice.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tms.transactionservice.dto.CreateComplaintRequest;
import com.tms.transactionservice.dto.ResolveComplaintRequest;
import com.tms.transactionservice.entity.Complaint;
import com.tms.transactionservice.entity.UserReference;
import com.tms.transactionservice.enums.ComplaintStatus;
import com.tms.transactionservice.exception.BusinessRuleException;
import com.tms.transactionservice.exception.ResourceNotFoundException;
import com.tms.transactionservice.repository.ComplaintRepository;
import com.tms.transactionservice.repository.UserReferenceRepository;
import com.tms.transactionservice.service.ComplaintService;

/** Implements complaint creation, access control, and resolution. */
@Service
@Transactional
public class ComplaintServiceImpl implements ComplaintService {
	private final UserReferenceRepository users;
	private final ComplaintRepository complaints;

	public ComplaintServiceImpl(UserReferenceRepository users, ComplaintRepository complaints) {
		this.users = users;
		this.complaints = complaints;
	}

	@Override
	public Complaint raiseComplaint(String username, CreateComplaintRequest request) {
		UserReference employee = currentUser(username);
		UserReference manager = users.findById(request.managerId())
				.orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
		if (!"MANAGER".equals(manager.getRole()))
			throw new BusinessRuleException("Complaint must be assigned to a manager");
		Complaint complaint = new Complaint();
		complaint.setEmployeeId(employee.getUserId());
		complaint.setManagerId(manager.getUserId());
		complaint.setSubject(request.subject());
		complaint.setDescription(request.description());
		return complaints.save(complaint);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Complaint> myComplaints(String username) {
		return complaints.findByEmployeeIdOrderByCreatedAtDesc(currentUser(username).getUserId());
	}

	@Override
	@Transactional(readOnly = true)
	public List<Complaint> assignedComplaints(String username) {
		return complaints.findByManagerIdOrderByCreatedAtDesc(currentUser(username).getUserId());
	}

	@Override
	public Complaint resolveComplaint(String username, Integer complaintId, ResolveComplaintRequest request) {
		Complaint complaint = complaints.findById(complaintId)
				.orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));
		if (!complaint.getManagerId().equals(currentUser(username).getUserId()))
			throw new BusinessRuleException("This complaint belongs to another manager");
		complaint.setResolution(request.resolution());
		complaint.setStatus(ComplaintStatus.RESOLVED);
		complaint.setResolvedAt(LocalDateTime.now());
		return complaint;
	}

	private UserReference currentUser(String username) {
		return users.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("Logged-in user no longer exists"));
	}
}
