package com.tms.transactionservice.service;

import java.util.List;
import com.tms.transactionservice.dto.CreateComplaintRequest;
import com.tms.transactionservice.dto.ResolveComplaintRequest;
import com.tms.transactionservice.dto.response.ComplaintResponse;
import com.tms.transactionservice.dto.response.UserSummaryResponse;

/** Contract used by ComplaintController. */
public interface ComplaintService {
    ComplaintResponse raiseComplaint(String username, CreateComplaintRequest request);
    List<ComplaintResponse> myComplaints(String username);
    List<ComplaintResponse> assignedComplaints(String username);
    List<UserSummaryResponse> availableManagers(String username);
    ComplaintResponse resolveComplaint(String username, Integer complaintId, ResolveComplaintRequest request);
}
