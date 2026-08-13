package com.tms.transactionservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.tms.transactionservice.dto.CreateComplaintRequest;
import com.tms.transactionservice.entity.UserReference;
import com.tms.transactionservice.repository.ComplaintRepository;
import com.tms.transactionservice.repository.EmployeeProjectReferenceRepository;
import com.tms.transactionservice.repository.UserReferenceRepository;
import com.tms.transactionservice.service.TransactionResponseMapper;
import com.tms.transactionservice.service.UserAccessService;

@ExtendWith(MockitoExtension.class)
class ComplaintServiceImplTest {

    @Mock UserAccessService userAccess;
    @Mock UserReferenceRepository users;
    @Mock EmployeeProjectReferenceRepository employeeProjects;
    @Mock ComplaintRepository complaints;
    @Mock TransactionResponseMapper mapper;

    private ComplaintServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ComplaintServiceImpl(
                userAccess, users, employeeProjects, complaints, mapper);
    }

    @Test
    void employeeCannotChooseUnrelatedManager() {
        UserReference employee = mock(UserReference.class);
        UserReference manager = mock(UserReference.class);
        when(employee.getUserId()).thenReturn(7);
        when(manager.getUserId()).thenReturn(5);
        when(userAccess.requireCurrentUser("emp1", "EMPLOYEE"))
                .thenReturn(employee);
        when(userAccess.requireUser(5, "MANAGER")).thenReturn(manager);
        when(employeeProjects.countAssignmentsWithManager(7, 5)).thenReturn(0L);

        assertThrows(
                AccessDeniedException.class,
                () -> service.raiseComplaint(
                        "emp1",
                        new CreateComplaintRequest(
                                5, "Access issue", "Unable to access the API")));
    }

}
