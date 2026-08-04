package com.tms.transactionservice.service;

import java.util.List;
import com.tms.transactionservice.dto.CreateComplaintRequest;
import com.tms.transactionservice.dto.ResolveComplaintRequest;
import com.tms.transactionservice.entity.Complaint;

/** Contract used by ComplaintController. */
public interface ComplaintService {
    Complaint raiseComplaint(String username, CreateComplaintRequest request);
    List<Complaint> myComplaints(String username);
    List<Complaint> assignedComplaints(String username);
    Complaint resolveComplaint(String username, Integer complaintId, ResolveComplaintRequest request);
}
