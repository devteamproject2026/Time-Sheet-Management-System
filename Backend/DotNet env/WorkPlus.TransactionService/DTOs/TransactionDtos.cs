using System;
using System.ComponentModel.DataAnnotations;

namespace WorkPlus.TransactionService.DTOs
{
    public class TaskRequest
    {
        public int ProjectId { get; set; }
        public int EmployeeId { get; set; }

        [Required, MaxLength(100)]
        public string TaskName { get; set; } = string.Empty;
        public string? TaskDescription { get; set; }
        public DateTime? StartDate { get; set; }
        public DateTime? EndDate { get; set; }
    }

    public class TaskStatusUpdateRequest
    {
        public string Status { get; set; } = string.Empty;
        public int ProgressPercent { get; set; }
        public string? Remarks { get; set; }
    }

    public class TaskResponse
    {
        public int TaskId { get; set; }
        public int ProjectId { get; set; }
        public int ManagerId { get; set; }
        public string ManagerName { get; set; } = string.Empty;
        public int EmployeeId { get; set; }
        public string EmployeeName { get; set; } = string.Empty;
        public string TaskName { get; set; } = string.Empty;
        public string? TaskDescription { get; set; }
        public DateTime? StartDate { get; set; }
        public DateTime? EndDate { get; set; }
        public string Status { get; set; } = string.Empty;
        public int ProgressPercent { get; set; }
        public string? Remarks { get; set; }
        public DateTime LastUpdated { get; set; }
        public DateTime CreatedAt { get; set; }
    }

    public class AttendanceRequest
    {
        public DateTime AttendanceDate { get; set; }
        public TimeSpan? CheckIn { get; set; }
        public TimeSpan? CheckOut { get; set; }
        public string Status { get; set; } = "PRESENT";
    }

    public class AttendanceResponse
    {
        public int AttendanceId { get; set; }
        public int EmployeeId { get; set; }
        public string EmployeeName { get; set; } = string.Empty;
        public DateTime AttendanceDate { get; set; }
        public TimeSpan? CheckIn { get; set; }
        public TimeSpan? CheckOut { get; set; }
        public string Status { get; set; } = string.Empty;
    }

    public class TimesheetRequest
    {
        public int TaskId { get; set; }
        public DateTime WorkDate { get; set; }
        public decimal HoursWorked { get; set; }
        public string? WorkDescription { get; set; }
    }

    public class TimesheetResponse
    {
        public int TimesheetId { get; set; }
        public int EmployeeId { get; set; }
        public string EmployeeName { get; set; } = string.Empty;
        public int TaskId { get; set; }
        public string TaskName { get; set; } = string.Empty;
        public DateTime WorkDate { get; set; }
        public decimal HoursWorked { get; set; }
        public string? WorkDescription { get; set; }
        public string Status { get; set; } = string.Empty;
        public DateTime SubmittedAt { get; set; }
    }

    public class TimesheetApprovalRequest
    {
        public int TimesheetId { get; set; }
        public string Status { get; set; } = "APPROVED"; // APPROVED / REJECTED
        public string? Comments { get; set; }
    }

    public class ComplaintRequest
    {
        [Required, MaxLength(100)]
        public string Subject { get; set; } = string.Empty;
        [Required]
        public string Description { get; set; } = string.Empty;
        public int? ManagerId { get; set; }
    }

    public class ResolveComplaintRequest
    {
        [Required]
        public string Resolution { get; set; } = string.Empty;
    }

    public class ComplaintResponse
    {
        public int ComplaintId { get; set; }
        public int EmployeeId { get; set; }
        public string EmployeeName { get; set; } = string.Empty;
        public int? ManagerId { get; set; }
        public string? ManagerName { get; set; }
        public string Subject { get; set; } = string.Empty;
        public string Description { get; set; } = string.Empty;
        public string Status { get; set; } = string.Empty;
        public string? Resolution { get; set; }
        public DateTime? ResolvedAt { get; set; }
        public DateTime CreatedAt { get; set; }
    }

    public class EmployeeReportResponse
    {
        public int EmployeeId { get; set; }
        public string EmployeeUsername { get; set; } = string.Empty;
        public string EmployeeFullName { get; set; } = string.Empty;
        public long TotalTasks { get; set; }
        public long CompletedTasks { get; set; }
        public double AverageProgress { get; set; }
        public decimal TotalApprovedHours { get; set; }
        public long PendingTimesheets { get; set; }
        public long ApprovedTimesheets { get; set; }
        public long RejectedTimesheets { get; set; }
    }
}
