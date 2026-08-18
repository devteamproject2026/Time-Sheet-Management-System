using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using WorkPlus.Shared.Enums;
using TaskStatus = WorkPlus.Shared.Enums.TaskStatus;

namespace WorkPlus.TransactionService.Models
{
    [Table("users")]
    public class UserReference
    {
        [Key]
        [Column("user_id")]
        public int UserId { get; set; }

        [Column("username")]
        public string Username { get; set; } = string.Empty;

        [Column("first_name")]
        public string FirstName { get; set; } = string.Empty;

        [Column("last_name")]
        public string LastName { get; set; } = string.Empty;

        [Column("email")]
        public string Email { get; set; } = string.Empty;

        [Column("role")]
        public Role Role { get; set; }
    }

    [Table("tasks")]
    public class TaskItem
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Column("task_id")]
        public int TaskId { get; set; }

        [Column("project_id")]
        public int ProjectId { get; set; }

        [Column("manager_id")]
        public int ManagerId { get; set; }

        [ForeignKey("ManagerId")]
        public virtual UserReference? Manager { get; set; }

        [Column("employee_id")]
        public int EmployeeId { get; set; }

        [ForeignKey("EmployeeId")]
        public virtual UserReference? Employee { get; set; }

        [Required, MaxLength(100)]
        [Column("task_name")]
        public string TaskName { get; set; } = string.Empty;

        [Column("task_description", TypeName = "TEXT")]
        public string? TaskDescription { get; set; }

        [Column("start_date")]
        public DateTime? StartDate { get; set; }

        [Column("end_date")]
        public DateTime? EndDate { get; set; }

        [Column("status")]
        public TaskStatus Status { get; set; } = TaskStatus.ASSIGNED;

        [Column("progress_percent")]
        public int ProgressPercent { get; set; } = 0;

        [Column("remarks", TypeName = "TEXT")]
        public string? Remarks { get; set; }

        [Column("last_updated")]
        public DateTime LastUpdated { get; set; } = DateTime.UtcNow;

        [Column("created_at")]
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    }

    [Table("attendance")]
    public class Attendance
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Column("attendance_id")]
        public int AttendanceId { get; set; }

        [Column("employee_id")]
        public int EmployeeId { get; set; }

        [ForeignKey("EmployeeId")]
        public virtual UserReference? Employee { get; set; }

        [Column("attendance_date")]
        public DateTime AttendanceDate { get; set; }

        [Column("check_in")]
        public TimeSpan? CheckIn { get; set; }

        [Column("check_out")]
        public TimeSpan? CheckOut { get; set; }

        [Column("status")]
        public AttendanceStatus Status { get; set; } = AttendanceStatus.PRESENT;
    }

    [Table("timesheets")]
    public class Timesheet
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Column("timesheet_id")]
        public int TimesheetId { get; set; }

        [Column("employee_id")]
        public int EmployeeId { get; set; }

        [ForeignKey("EmployeeId")]
        public virtual UserReference? Employee { get; set; }

        [Column("task_id")]
        public int TaskId { get; set; }

        [ForeignKey("TaskId")]
        public virtual TaskItem? Task { get; set; }

        [Column("work_date")]
        public DateTime WorkDate { get; set; }

        [Column("hours_worked", TypeName = "decimal(4,2)")]
        public decimal HoursWorked { get; set; }

        [Column("work_description", TypeName = "TEXT")]
        public string? WorkDescription { get; set; }

        [Column("status")]
        public TimesheetStatus Status { get; set; } = TimesheetStatus.PENDING;

        [Column("submitted_at")]
        public DateTime SubmittedAt { get; set; } = DateTime.UtcNow;
    }

    [Table("timesheet_approvals")]
    public class TimesheetApproval
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Column("approval_id")]
        public int ApprovalId { get; set; }

        [Column("timesheet_id")]
        public int TimesheetId { get; set; }

        [ForeignKey("TimesheetId")]
        public virtual Timesheet? Timesheet { get; set; }

        [Column("manager_id")]
        public int ManagerId { get; set; }

        [ForeignKey("ManagerId")]
        public virtual UserReference? Manager { get; set; }

        [Column("approval_status")]
        public TimesheetStatus ApprovalStatus { get; set; }

        [MaxLength(500)]
        [Column("comments")]
        public string? Comments { get; set; }

        [Column("approval_date")]
        public DateTime ApprovalDate { get; set; } = DateTime.UtcNow;
    }

    [Table("complaints")]
    public class Complaint
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Column("complaint_id")]
        public int ComplaintId { get; set; }

        [Column("employee_id")]
        public int EmployeeId { get; set; }

        [ForeignKey("EmployeeId")]
        public virtual UserReference? Employee { get; set; }

        [Column("manager_id")]
        public int? ManagerId { get; set; }

        [ForeignKey("ManagerId")]
        public virtual UserReference? Manager { get; set; }

        [Required, MaxLength(100)]
        [Column("subject")]
        public string Subject { get; set; } = string.Empty;

        [Column("description", TypeName = "TEXT")]
        public string Description { get; set; } = string.Empty;

        [Column("status")]
        public ComplaintStatus Status { get; set; } = ComplaintStatus.OPEN;

        [Column("resolution", TypeName = "TEXT")]
        public string? Resolution { get; set; }

        [Column("resolved_at")]
        public DateTime? ResolvedAt { get; set; }

        [Column("created_at")]
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    }
}
