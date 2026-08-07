package com.tms.transactionservice.service;

import org.springframework.stereotype.Component;

import com.tms.transactionservice.dto.response.AttendanceResponse;
import com.tms.transactionservice.dto.response.ComplaintResponse;
import com.tms.transactionservice.dto.response.TaskResponse;
import com.tms.transactionservice.dto.response.TimesheetResponse;
import com.tms.transactionservice.dto.response.UserSummaryResponse;
import com.tms.transactionservice.entity.Attendance;
import com.tms.transactionservice.entity.Complaint;
import com.tms.transactionservice.entity.ProjectReference;
import com.tms.transactionservice.entity.Task;
import com.tms.transactionservice.entity.Timesheet;
import com.tms.transactionservice.entity.UserReference;
import com.tms.transactionservice.exception.ResourceNotFoundException;
import com.tms.transactionservice.repository.ProjectReferenceRepository;
import com.tms.transactionservice.repository.TaskRepository;
import com.tms.transactionservice.repository.UserReferenceRepository;

/** Converts persistence entities into safe, frontend-friendly responses. */
@Component
public class TransactionResponseMapper {

    private final UserReferenceRepository users;
    private final ProjectReferenceRepository projects;
    private final TaskRepository tasks;

    public TransactionResponseMapper(
            UserReferenceRepository users,
            ProjectReferenceRepository projects,
            TaskRepository tasks) {
        this.users = users;
        this.projects = projects;
        this.tasks = tasks;
    }

    public UserSummaryResponse toUserSummary(UserReference user) {
        return new UserSummaryResponse(
                user.getUserId(),
                user.getUsername(),
                fullName(user));
    }

    public TaskResponse toTaskResponse(Task task) {
        ProjectReference project = project(task.getProjectId());
        UserReference manager = user(task.getManagerId());
        UserReference employee = user(task.getEmployeeId());

        return new TaskResponse(
                task.getTaskId(),
                task.getProjectId(),
                project.getProjectName(),
                task.getManagerId(),
                manager.getUsername(),
                task.getEmployeeId(),
                employee.getUsername(),
                fullName(employee),
                task.getTaskName(),
                task.getTaskDescription(),
                task.getStartDate(),
                task.getEndDate(),
                task.getStatus(),
                task.getProgressPercent(),
                task.getRemarks(),
                task.getLastUpdated(),
                task.getCreatedAt());
    }

    public TimesheetResponse toTimesheetResponse(Timesheet timesheet) {
        Task task = tasks.findById(timesheet.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found for timesheet: " + timesheet.getTimesheetId()));
        ProjectReference project = project(task.getProjectId());
        UserReference employee = user(timesheet.getEmployeeId());

        return new TimesheetResponse(
                timesheet.getTimesheetId(),
                timesheet.getEmployeeId(),
                employee.getUsername(),
                fullName(employee),
                timesheet.getTaskId(),
                task.getTaskName(),
                task.getProjectId(),
                project.getProjectName(),
                timesheet.getWorkDate(),
                timesheet.getHoursWorked(),
                timesheet.getWorkDescription(),
                timesheet.getStatus(),
                timesheet.getSubmittedAt());
    }

    public ComplaintResponse toComplaintResponse(Complaint complaint) {
        UserReference employee = user(complaint.getEmployeeId());
        UserReference manager = user(complaint.getManagerId());

        return new ComplaintResponse(
                complaint.getComplaintId(),
                complaint.getEmployeeId(),
                employee.getUsername(),
                fullName(employee),
                complaint.getManagerId(),
                manager.getUsername(),
                fullName(manager),
                complaint.getSubject(),
                complaint.getDescription(),
                complaint.getStatus(),
                complaint.getResolution(),
                complaint.getResolvedAt(),
                complaint.getCreatedAt());
    }

    public AttendanceResponse toAttendanceResponse(Attendance attendance) {
        UserReference employee = user(attendance.getEmployeeId());

        return new AttendanceResponse(
                attendance.getAttendanceId(),
                attendance.getEmployeeId(),
                employee.getUsername(),
                fullName(employee),
                attendance.getAttendanceDate(),
                attendance.getCheckIn(),
                attendance.getCheckOut(),
                attendance.getStatus());
    }

    private UserReference user(Integer userId) {
        return users.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Referenced user not found: " + userId));
    }

    private ProjectReference project(Integer projectId) {
        return projects.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Referenced project not found: " + projectId));
    }

    private String fullName(UserReference user) {
        return (user.getFirstName() + " " + user.getLastName()).trim();
    }
}
