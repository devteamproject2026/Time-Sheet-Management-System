package com.tms.businessservice.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tms.businessservice.dto.request.ProjectRequest;
import com.tms.businessservice.dto.response.ProjectResponse;
import com.tms.businessservice.entity.Client;
import com.tms.businessservice.entity.Project;
import com.tms.businessservice.entity.Users;
import com.tms.businessservice.enums.ProjectStatus;
import com.tms.businessservice.exception.BusinessValidationException;
import com.tms.businessservice.exception.ResourceNotFoundException;
import com.tms.businessservice.repository.ClientRepository;
import com.tms.businessservice.repository.ProjectRepository;
import com.tms.businessservice.repository.UsersRepository;
import com.tms.businessservice.service.ProjectService;

/**
 * Implements Project CRUD and the business rules that must be checked before
 * project data is saved in MySQL.
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    private static final String MANAGER_ROLE = "MANAGER";
    private static final String EMPLOYEE_ROLE = "EMPLOYEE";
    private static final String HR_HEAD_ROLE = "HR_HEAD";
    private static final String ACTIVE_STATUS = "ACTIVE";
    private static final String APPROVED_STATUS = "APPROVED";

    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;
    private final UsersRepository usersRepository;

    /**
     * Constructor injection clearly shows every repository required by this
     * service and also makes the class easier to test.
     */
    public ProjectServiceImpl(
            ProjectRepository projectRepository,
            ClientRepository clientRepository,
            UsersRepository usersRepository) {

        this.projectRepository = projectRepository;
        this.clientRepository = clientRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {

        validateDates(request.getStartDate(), request.getEndDate());

        // Load and validate every foreign-key record before saving the Project.
        Client client = findClientOrThrow(request.getClientId());
        Users manager = findAssignableUser(
                request.getManagerId(),
                MANAGER_ROLE,
                "Manager");
        Users hrHead = findAssignableUser(
                request.getHrHeadId(),
                HR_HEAD_ROLE,
                "HR Head");

        Project project = Project.builder()
                .projectName(request.getProjectName().trim())
                .description(request.getDescription())
                .client(client)
                .manager(manager)
                .hrHead(hrHead)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                // ACTIVE is the SQL and application default for new Projects.
                .status(request.getStatus() == null
                        ? ProjectStatus.ACTIVE
                        : request.getStatus())
                .build();

        Project savedProject = projectRepository.save(project);
        return toResponse(savedProject);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects() {

        return projectRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Returns only Projects owned by the logged-in Manager.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getMyManagedProjects(
            String managerUsername) {

        Users manager = findCurrentUserWithRole(
                managerUsername,
                MANAGER_ROLE,
                "Manager");

        return projectRepository
                .findByManager_UsernameOrderByCreatedAtDesc(
                        manager.getUsername())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Returns only Projects connected to the logged-in Employee through the
     * employee_projects table.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getMyAssignedProjects(
            String employeeUsername) {

        Users employee = findCurrentUserWithRole(
                employeeUsername,
                EMPLOYEE_ROLE,
                "Employee");

        return projectRepository
                .findAssignedProjectsByEmployeeUsername(
                        employee.getUsername())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Integer projectId) {

        return toResponse(findProjectOrThrow(projectId));
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(
            Integer projectId,
            ProjectRequest request) {

        validateDates(request.getStartDate(), request.getEndDate());

        Project project = findProjectOrThrow(projectId);
        Client client = findClientOrThrow(request.getClientId());
        Users manager = findAssignableUser(
                request.getManagerId(),
                MANAGER_ROLE,
                "Manager");
        Users hrHead = findAssignableUser(
                request.getHrHeadId(),
                HR_HEAD_ROLE,
                "HR Head");

        project.setProjectName(request.getProjectName().trim());
        project.setDescription(request.getDescription());
        project.setClient(client);
        project.setManager(manager);
        project.setHrHead(hrHead);
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());

        // When status is omitted during PUT, keep the existing status instead
        // of unexpectedly changing the Project back to ACTIVE.
        if (request.getStatus() != null) {
            project.setStatus(request.getStatus());
        }

        Project updatedProject = projectRepository.save(project);
        return toResponse(updatedProject);
    }

    @Override
    @Transactional
    public void deleteProject(Integer projectId) {

        // Finding first produces a clear 404 when the ID does not exist.
        Project project = findProjectOrThrow(projectId);
        projectRepository.delete(project);
    }

    /**
     * The end date cannot be earlier than the start date.
     *
     * Either date may be null because both columns are optional in the SQL.
     */
    private void validateDates(LocalDate startDate, LocalDate endDate) {

        if (startDate != null
                && endDate != null
                && endDate.isBefore(startDate)) {

            throw new BusinessValidationException(
                    "Project end date cannot be before the start date");
        }
    }

    private Project findProjectOrThrow(Integer projectId) {

        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project not found with ID: " + projectId));
    }

    private Client findClientOrThrow(Integer clientId) {

        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client not found with ID: " + clientId));
    }

    /**
     * Rechecks the JWT username against MySQL before returning role-scoped
     * Project data. This prevents an inactive account from using an older JWT.
     */
    private Users findCurrentUserWithRole(
            String username,
            String requiredRole,
            String userLabel) {

        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Logged-in user was not found"));

        if (!requiredRole.equals(user.getRole())) {
            throw new AccessDeniedException(
                    "Only a " + userLabel
                            + " can use this Project operation");
        }

        if (!ACTIVE_STATUS.equals(user.getAccountStatus())
                || !APPROVED_STATUS.equals(user.getApprovalStatus())) {

            throw new AccessDeniedException(
                    userLabel + " account must be approved and active");
        }

        return user;
    }

    /**
     * Confirms that a related user exists, has the required role, and is
     * currently approved and active.
     */
    private Users findAssignableUser(
            Integer userId,
            String requiredRole,
            String userLabel) {

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        userLabel + " not found with ID: " + userId));

        if (!requiredRole.equals(user.getRole())) {
            throw new BusinessValidationException(
                    userLabel + " ID " + userId
                            + " does not have role " + requiredRole);
        }

        if (!ACTIVE_STATUS.equals(user.getAccountStatus())
                || !APPROVED_STATUS.equals(user.getApprovalStatus())) {

            throw new BusinessValidationException(
                    userLabel + " ID " + userId
                            + " must be approved and active");
        }

        return user;
    }

    /**
     * Converts the internal Project and its relationships into safe API data.
     */
    private ProjectResponse toResponse(Project project) {

        return ProjectResponse.builder()
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .description(project.getDescription())
                .clientId(project.getClient().getClientId())
                .clientName(project.getClient().getClientName())
                .managerId(project.getManager().getUserId())
                .managerUsername(project.getManager().getUsername())
                .hrHeadId(project.getHrHead().getUserId())
                .hrHeadUsername(project.getHrHead().getUsername())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .status(project.getStatus())
                .createdAt(project.getCreatedAt())
                .build();
    }
}
