package com.tms.businessservice.service.impl;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tms.businessservice.dto.request.EmployeeProjectRequest;
import com.tms.businessservice.dto.response.EmployeeProjectResponse;
import com.tms.businessservice.entity.EmployeeProject;
import com.tms.businessservice.entity.Project;
import com.tms.businessservice.entity.Users;
import com.tms.businessservice.exception.BusinessValidationException;
import com.tms.businessservice.exception.ResourceConflictException;
import com.tms.businessservice.exception.ResourceNotFoundException;
import com.tms.businessservice.repository.EmployeeProjectRepository;
import com.tms.businessservice.repository.ProjectRepository;
import com.tms.businessservice.repository.UsersRepository;
import com.tms.businessservice.service.EmployeeProjectService;

/**
 * Implements Employee-to-Project assignment rules and safe response mapping.
 */
@Service
public class EmployeeProjectServiceImpl implements EmployeeProjectService {

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String HR_HEAD_ROLE = "HR_HEAD";
    private static final String MANAGER_ROLE = "MANAGER";
    private static final String EMPLOYEE_ROLE = "EMPLOYEE";
    private static final String ACTIVE_STATUS = "ACTIVE";
    private static final String APPROVED_STATUS = "APPROVED";

    private final EmployeeProjectRepository employeeProjectRepository;
    private final ProjectRepository projectRepository;
    private final UsersRepository usersRepository;

    public EmployeeProjectServiceImpl(
            EmployeeProjectRepository employeeProjectRepository,
            ProjectRepository projectRepository,
            UsersRepository usersRepository) {

        this.employeeProjectRepository = employeeProjectRepository;
        this.projectRepository = projectRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    @Transactional
    public EmployeeProjectResponse assignEmployee(
            EmployeeProjectRequest request,
            String actorUsername) {

        Users actor = findActiveUserByUsername(actorUsername);
        Project project = findProjectOrThrow(request.getProjectId());

        // ADMIN/HR can use any Project. A MANAGER can use only a Project whose
        // manager_id matches that logged-in Manager's user ID.
        ensureCanManageProject(actor, project);

        Users employee = findActiveEmployeeOrThrow(request.getEmployeeId());

        if (employeeProjectRepository
                .existsByEmployee_UserIdAndProject_ProjectId(
                        employee.getUserId(),
                        project.getProjectId())) {

            throw new ResourceConflictException(
                    "Employee ID " + employee.getUserId()
                            + " is already assigned to Project ID "
                            + project.getProjectId());
        }

        EmployeeProject assignment = EmployeeProject.builder()
                .employee(employee)
                .project(project)
                .build();

        EmployeeProject savedAssignment =
                employeeProjectRepository.save(assignment);

        return toResponse(savedAssignment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeProjectResponse> getAllAssignments(
            String actorUsername) {

        Users actor = findActiveUserByUsername(actorUsername);
        ensureAdminOrHr(actor);

        return employeeProjectRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeProjectResponse> getAssignmentsByProject(
            Integer projectId,
            String actorUsername) {

        Users actor = findActiveUserByUsername(actorUsername);
        Project project = findProjectOrThrow(projectId);
        ensureCanManageProject(actor, project);

        return employeeProjectRepository
                .findByProject_ProjectIdOrderByAssignedDateDesc(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeProjectResponse> getAssignmentsByEmployee(
            Integer employeeId,
            String actorUsername) {

        Users actor = findActiveUserByUsername(actorUsername);
        ensureAdminOrHr(actor);

        // Return 404 for a missing ID and a clear 400 if it is not an Employee.
        Users employee = findActiveEmployeeOrThrow(employeeId);

        return employeeProjectRepository
                .findByEmployee_UserIdOrderByAssignedDateDesc(
                        employee.getUserId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeProjectResponse> getMyAssignments(
            String actorUsername) {

        Users actor = findActiveUserByUsername(actorUsername);

        if (!EMPLOYEE_ROLE.equals(actor.getRole())) {
            throw new AccessDeniedException(
                    "Only an Employee can use the my-projects operation");
        }

        return employeeProjectRepository
                .findByEmployee_UserIdOrderByAssignedDateDesc(
                        actor.getUserId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void removeAssignment(
            Integer employeeProjectId,
            String actorUsername) {

        Users actor = findActiveUserByUsername(actorUsername);
        EmployeeProject assignment =
                findAssignmentOrThrow(employeeProjectId);

        // A Manager may remove only assignments from their own Project.
        ensureCanManageProject(actor, assignment.getProject());

        employeeProjectRepository.delete(assignment);
    }

    /**
     * Allows system-wide operations for ADMIN/HR and Project-scoped operations
     * for the Manager who actually owns that Project.
     */
    private void ensureCanManageProject(Users actor, Project project) {

        if (isAdminOrHr(actor)) {
            return;
        }

        boolean isOwningManager =
                MANAGER_ROLE.equals(actor.getRole())
                        && project.getManager().getUserId()
                                == actor.getUserId();

        if (!isOwningManager) {
            throw new AccessDeniedException(
                    "You can manage assignments only for your own Project");
        }
    }

    private void ensureAdminOrHr(Users actor) {

        if (!isAdminOrHr(actor)) {
            throw new AccessDeniedException(
                    "Only Admin or HR Head can view all assignments");
        }
    }

    private boolean isAdminOrHr(Users user) {

        return ADMIN_ROLE.equals(user.getRole())
                || HR_HEAD_ROLE.equals(user.getRole());
    }

    private Users findActiveEmployeeOrThrow(Integer employeeId) {

        Users employee = usersRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with ID: " + employeeId));

        if (!EMPLOYEE_ROLE.equals(employee.getRole())) {
            throw new BusinessValidationException(
                    "User ID " + employeeId
                            + " does not have role EMPLOYEE");
        }

        validateActiveAccount(employee, "Employee ID " + employeeId);
        return employee;
    }

    /**
     * Loads the current actor again from MySQL. Therefore, an old JWT cannot
     * continue operating after the account becomes inactive.
     */
    private Users findActiveUserByUsername(String username) {

        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Logged-in user was not found"));

        validateActiveAccount(user, "Logged-in user");
        return user;
    }

    private void validateActiveAccount(Users user, String userLabel) {

        if (!ACTIVE_STATUS.equals(user.getAccountStatus())
                || !APPROVED_STATUS.equals(user.getApprovalStatus())) {

            throw new BusinessValidationException(
                    userLabel + " must be approved and active");
        }
    }

    private Project findProjectOrThrow(Integer projectId) {

        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project not found with ID: " + projectId));
    }

    private EmployeeProject findAssignmentOrThrow(
            Integer employeeProjectId) {

        return employeeProjectRepository.findById(employeeProjectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee-Project assignment not found with ID: "
                                + employeeProjectId));
    }

    private EmployeeProjectResponse toResponse(EmployeeProject assignment) {

        Users employee = assignment.getEmployee();
        Project project = assignment.getProject();

        String fullName =
                (employee.getFirstName() + " " + employee.getLastName())
                        .trim();

        return EmployeeProjectResponse.builder()
                .employeeProjectId(assignment.getEmployeeProjectId())
                .employeeId(employee.getUserId())
                .employeeUsername(employee.getUsername())
                .employeeFullName(fullName)
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .managerId(project.getManager().getUserId())
                .managerUsername(project.getManager().getUsername())
                .assignedDate(assignment.getAssignedDate())
                .build();
    }
}
