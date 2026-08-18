using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using WorkPlus.Shared.Enums;
using WorkPlus.TransactionService.Data;
using WorkPlus.TransactionService.DTOs;
using WorkPlus.TransactionService.Models;
using TaskStatus = WorkPlus.Shared.Enums.TaskStatus;

namespace WorkPlus.TransactionService.Services
{
    public class TransactionServiceImpl : ITransactionService
    {
        private readonly TransactionDbContext _context;

        public TransactionServiceImpl(TransactionDbContext context)
        {
            _context = context;
        }

        // --- Tasks ---
        public async Task<TaskResponse> CreateTaskAsync(TaskRequest request, string managerUsername)
        {
            var manager = await _context.Users.FirstOrDefaultAsync(u => u.Username == managerUsername);
            if (manager == null) throw new UnauthorizedAccessException("Manager not found");

            var task = new TaskItem
            {
                ProjectId = request.ProjectId,
                ManagerId = manager.UserId,
                EmployeeId = request.EmployeeId,
                TaskName = request.TaskName,
                TaskDescription = request.TaskDescription,
                StartDate = request.StartDate,
                EndDate = request.EndDate,
                Status = TaskStatus.ASSIGNED,
                ProgressPercent = 0,
                CreatedAt = DateTime.UtcNow,
                LastUpdated = DateTime.UtcNow
            };

            _context.Tasks.Add(task);
            await _context.SaveChangesAsync();

            var saved = await _context.Tasks
                .Include(t => t.Manager)
                .Include(t => t.Employee)
                .FirstAsync(t => t.TaskId == task.TaskId);

            return MapTaskToResponse(saved);
        }

        public async Task<List<TaskResponse>> GetTasksForEmployeeAsync(string employeeUsername)
        {
            var emp = await _context.Users.FirstOrDefaultAsync(u => u.Username == employeeUsername);
            if (emp == null) return new List<TaskResponse>();

            return await _context.Tasks
                .Include(t => t.Manager)
                .Include(t => t.Employee)
                .Where(t => t.EmployeeId == emp.UserId)
                .Select(t => MapTaskToResponse(t))
                .ToListAsync();
        }

        public async Task<List<TaskResponse>> GetTasksForManagerAsync(string managerUsername)
        {
            var mgr = await _context.Users.FirstOrDefaultAsync(u => u.Username == managerUsername);
            if (mgr == null) return new List<TaskResponse>();

            return await _context.Tasks
                .Include(t => t.Manager)
                .Include(t => t.Employee)
                .Where(t => t.ManagerId == mgr.UserId)
                .Select(t => MapTaskToResponse(t))
                .ToListAsync();
        }

        public async Task<TaskResponse> UpdateTaskStatusAsync(int taskId, TaskStatusUpdateRequest request, string username)
        {
            var task = await _context.Tasks.Include(t => t.Manager).Include(t => t.Employee).FirstOrDefaultAsync(t => t.TaskId == taskId);
            if (task == null) throw new KeyNotFoundException("Task not found");

            Enum.TryParse<TaskStatus>(request.Status, true, out var status);
            task.Status = status;
            task.ProgressPercent = request.ProgressPercent;
            task.Remarks = request.Remarks;
            task.LastUpdated = DateTime.UtcNow;

            await _context.SaveChangesAsync();
            return MapTaskToResponse(task);
        }

        // --- Attendance ---
        public async Task<AttendanceResponse> MarkAttendanceAsync(AttendanceRequest request, string username)
        {
            var emp = await _context.Users.FirstOrDefaultAsync(u => u.Username == username);
            if (emp == null) throw new UnauthorizedAccessException("Employee not found");

            Enum.TryParse<AttendanceStatus>(request.Status, true, out var status);

            var existing = await _context.Attendances.FirstOrDefaultAsync(a => a.EmployeeId == emp.UserId && a.AttendanceDate.Date == request.AttendanceDate.Date);
            if (existing != null)
            {
                if (request.CheckOut.HasValue) existing.CheckOut = request.CheckOut;
                if (request.CheckIn.HasValue) existing.CheckIn = request.CheckIn;
                existing.Status = status;
                await _context.SaveChangesAsync();
                return MapAttendanceToResponse(existing, emp);
            }

            var attendance = new Attendance
            {
                EmployeeId = emp.UserId,
                AttendanceDate = request.AttendanceDate.Date,
                CheckIn = request.CheckIn ?? DateTime.Now.TimeOfDay,
                CheckOut = request.CheckOut,
                Status = status
            };

            _context.Attendances.Add(attendance);
            await _context.SaveChangesAsync();

            return MapAttendanceToResponse(attendance, emp);
        }

        public async Task<List<AttendanceResponse>> GetAttendanceForEmployeeAsync(string username)
        {
            var emp = await _context.Users.FirstOrDefaultAsync(u => u.Username == username);
            if (emp == null) return new List<AttendanceResponse>();

            return await _context.Attendances
                .Include(a => a.Employee)
                .Where(a => a.EmployeeId == emp.UserId)
                .Select(a => MapAttendanceToResponse(a, a.Employee!))
                .ToListAsync();
        }

        // --- Timesheets ---
        public async Task<TimesheetResponse> SubmitTimesheetAsync(TimesheetRequest request, string employeeUsername)
        {
            var emp = await _context.Users.FirstOrDefaultAsync(u => u.Username == employeeUsername);
            if (emp == null) throw new UnauthorizedAccessException("Employee not found");

            var timesheet = new Timesheet
            {
                EmployeeId = emp.UserId,
                TaskId = request.TaskId,
                WorkDate = request.WorkDate,
                HoursWorked = request.HoursWorked,
                WorkDescription = request.WorkDescription,
                Status = TimesheetStatus.PENDING,
                SubmittedAt = DateTime.UtcNow
            };

            _context.Timesheets.Add(timesheet);
            await _context.SaveChangesAsync();

            var saved = await _context.Timesheets
                .Include(t => t.Employee)
                .Include(t => t.Task)
                .FirstAsync(t => t.TimesheetId == timesheet.TimesheetId);

            return MapTimesheetToResponse(saved);
        }

        public async Task<List<TimesheetResponse>> GetTimesheetsForEmployeeAsync(string employeeUsername)
        {
            var emp = await _context.Users.FirstOrDefaultAsync(u => u.Username == employeeUsername);
            if (emp == null) return new List<TimesheetResponse>();

            return await _context.Timesheets
                .Include(t => t.Employee)
                .Include(t => t.Task)
                .Where(t => t.EmployeeId == emp.UserId)
                .Select(t => MapTimesheetToResponse(t))
                .ToListAsync();
        }

        public async Task<List<TimesheetResponse>> GetPendingTimesheetsForManagerAsync(string managerUsername)
        {
            var mgr = await _context.Users.FirstOrDefaultAsync(u => u.Username == managerUsername);
            if (mgr == null) return new List<TimesheetResponse>();

            var managerTaskIds = _context.Tasks
                .Where(t => t.ManagerId == mgr.UserId)
                .Select(t => t.TaskId);

            return await _context.Timesheets
                .Include(t => t.Employee)
                .Include(t => t.Task)
                .Where(t => managerTaskIds.Contains(t.TaskId) && t.Status == TimesheetStatus.PENDING)
                .Select(t => MapTimesheetToResponse(t))
                .ToListAsync();
        }

        public async Task<string> ProcessTimesheetApprovalAsync(TimesheetApprovalRequest request, string managerUsername)
        {
            var mgr = await _context.Users.FirstOrDefaultAsync(u => u.Username == managerUsername);
            if (mgr == null) throw new UnauthorizedAccessException("Manager not found");

            var timesheet = await _context.Timesheets.FindAsync(request.TimesheetId);
            if (timesheet == null) throw new KeyNotFoundException("Timesheet not found");

            Enum.TryParse<TimesheetStatus>(request.Status, true, out var status);
            timesheet.Status = status;

            var approval = new TimesheetApproval
            {
                TimesheetId = timesheet.TimesheetId,
                ManagerId = mgr.UserId,
                ApprovalStatus = status,
                Comments = request.Comments,
                ApprovalDate = DateTime.UtcNow
            };

            _context.TimesheetApprovals.Add(approval);
            await _context.SaveChangesAsync();

            return $"Timesheet {status.ToString().ToLower()} successfully.";
        }

        // --- Complaints ---
        public async Task<ComplaintResponse> CreateComplaintAsync(ComplaintRequest request, string employeeUsername)
        {
            var emp = await _context.Users.FirstOrDefaultAsync(u => u.Username == employeeUsername);
            if (emp == null) throw new UnauthorizedAccessException("Employee not found");

            var complaint = new Complaint
            {
                EmployeeId = emp.UserId,
                ManagerId = request.ManagerId,
                Subject = request.Subject,
                Description = request.Description,
                Status = ComplaintStatus.OPEN,
                CreatedAt = DateTime.UtcNow
            };

            _context.Complaints.Add(complaint);
            await _context.SaveChangesAsync();

            var saved = await _context.Complaints.Include(c => c.Employee).Include(c => c.Manager).FirstAsync(c => c.ComplaintId == complaint.ComplaintId);
            return MapComplaintToResponse(saved);
        }

        public async Task<List<ComplaintResponse>> GetComplaintsForEmployeeAsync(string employeeUsername)
        {
            var emp = await _context.Users.FirstOrDefaultAsync(u => u.Username == employeeUsername);
            if (emp == null) return new List<ComplaintResponse>();

            return await _context.Complaints
                .Include(c => c.Employee)
                .Include(c => c.Manager)
                .Where(c => c.EmployeeId == emp.UserId)
                .Select(c => MapComplaintToResponse(c))
                .ToListAsync();
        }

        public async Task<List<ComplaintResponse>> GetAllComplaintsForAdminAsync()
        {
            return await _context.Complaints
                .Include(c => c.Employee)
                .Include(c => c.Manager)
                .Select(c => MapComplaintToResponse(c))
                .ToListAsync();
        }

        public async Task<ComplaintResponse> ResolveComplaintAsync(int complaintId, ResolveComplaintRequest request, string managerUsername)
        {
            var complaint = await _context.Complaints.Include(c => c.Employee).Include(c => c.Manager).FirstOrDefaultAsync(c => c.ComplaintId == complaintId);
            if (complaint == null) throw new KeyNotFoundException("Complaint not found");

            complaint.Resolution = request.Resolution;
            complaint.Status = ComplaintStatus.RESOLVED;
            complaint.ResolvedAt = DateTime.UtcNow;

            await _context.SaveChangesAsync();
            return MapComplaintToResponse(complaint);
        }

        // --- Employee Reports ---
        public async Task<List<EmployeeReportResponse>> GetEmployeeReportsForManagerAsync(string managerUsername)
        {
            var mgr = await _context.Users.FirstOrDefaultAsync(u => u.Username == managerUsername);
            if (mgr == null) return new List<EmployeeReportResponse>();

            var employeeIds = await _context.Tasks
                .Where(t => t.ManagerId == mgr.UserId)
                .Select(t => t.EmployeeId)
                .Distinct()
                .ToListAsync();

            var reports = new List<EmployeeReportResponse>();
            foreach (var empId in employeeIds)
            {
                var report = await BuildEmployeeReportAsync(mgr.UserId, empId);
                if (report != null) reports.Add(report);
            }

            return reports;
        }

        public async Task<EmployeeReportResponse?> GetEmployeeReportForManagerAsync(string managerUsername, int employeeId)
        {
            var mgr = await _context.Users.FirstOrDefaultAsync(u => u.Username == managerUsername);
            if (mgr == null) return null;

            return await BuildEmployeeReportAsync(mgr.UserId, employeeId);
        }

        private async Task<EmployeeReportResponse?> BuildEmployeeReportAsync(int managerId, int employeeId)
        {
            var employee = await _context.Users.FindAsync(employeeId);
            if (employee == null) return null;

            var empTasks = await _context.Tasks
                .Where(t => t.ManagerId == managerId && t.EmployeeId == employeeId)
                .ToListAsync();

            var empTaskIdsQuery = _context.Tasks
                .Where(t => t.ManagerId == managerId && t.EmployeeId == employeeId)
                .Select(t => t.TaskId);

            var empTimesheets = await _context.Timesheets
                .Where(ts => empTaskIdsQuery.Contains(ts.TaskId))
                .ToListAsync();

            long completedTasks = empTasks.Count(t => t.Status == TaskStatus.COMPLETED);
            double avgProgress = empTasks.Count > 0 ? empTasks.Average(t => t.ProgressPercent) : 0.0;

            decimal approvedHours = empTimesheets
                .Where(ts => ts.Status == TimesheetStatus.APPROVED)
                .Sum(ts => ts.HoursWorked);

            long pendingTs = empTimesheets.Count(ts => ts.Status == TimesheetStatus.PENDING);
            long approvedTs = empTimesheets.Count(ts => ts.Status == TimesheetStatus.APPROVED);
            long rejectedTs = empTimesheets.Count(ts => ts.Status == TimesheetStatus.REJECTED);

            return new EmployeeReportResponse
            {
                EmployeeId = employee.UserId,
                EmployeeUsername = employee.Username,
                EmployeeFullName = $"{employee.FirstName} {employee.LastName}".Trim(),
                TotalTasks = empTasks.Count,
                CompletedTasks = completedTasks,
                AverageProgress = Math.Round(avgProgress, 2),
                TotalApprovedHours = approvedHours,
                PendingTimesheets = pendingTs,
                ApprovedTimesheets = approvedTs,
                RejectedTimesheets = rejectedTs
            };
        }

        // Mappers
        private static TaskResponse MapTaskToResponse(TaskItem t) => new()
        {
            TaskId = t.TaskId,
            ProjectId = t.ProjectId,
            ManagerId = t.ManagerId,
            ManagerName = t.Manager != null ? $"{t.Manager.FirstName} {t.Manager.LastName}" : string.Empty,
            EmployeeId = t.EmployeeId,
            EmployeeName = t.Employee != null ? $"{t.Employee.FirstName} {t.Employee.LastName}" : string.Empty,
            TaskName = t.TaskName,
            TaskDescription = t.TaskDescription,
            StartDate = t.StartDate,
            EndDate = t.EndDate,
            Status = t.Status.ToString(),
            ProgressPercent = t.ProgressPercent,
            Remarks = t.Remarks,
            LastUpdated = t.LastUpdated,
            CreatedAt = t.CreatedAt
        };

        private static AttendanceResponse MapAttendanceToResponse(Attendance a, UserReference emp) => new()
        {
            AttendanceId = a.AttendanceId,
            EmployeeId = a.EmployeeId,
            EmployeeName = $"{emp.FirstName} {emp.LastName}",
            AttendanceDate = a.AttendanceDate,
            CheckIn = a.CheckIn,
            CheckOut = a.CheckOut,
            Status = a.Status.ToString()
        };

        private static TimesheetResponse MapTimesheetToResponse(Timesheet t) => new()
        {
            TimesheetId = t.TimesheetId,
            EmployeeId = t.EmployeeId,
            EmployeeName = t.Employee != null ? $"{t.Employee.FirstName} {t.Employee.LastName}" : string.Empty,
            TaskId = t.TaskId,
            TaskName = t.Task?.TaskName ?? string.Empty,
            WorkDate = t.WorkDate,
            HoursWorked = t.HoursWorked,
            WorkDescription = t.WorkDescription,
            Status = t.Status.ToString(),
            SubmittedAt = t.SubmittedAt
        };

        private static ComplaintResponse MapComplaintToResponse(Complaint c) => new()
        {
            ComplaintId = c.ComplaintId,
            EmployeeId = c.EmployeeId,
            EmployeeName = c.Employee != null ? $"{c.Employee.FirstName} {c.Employee.LastName}" : string.Empty,
            ManagerId = c.ManagerId,
            ManagerName = c.Manager != null ? $"{c.Manager.FirstName} {c.Manager.LastName}" : null,
            Subject = c.Subject,
            Description = c.Description,
            Status = c.Status.ToString(),
            Resolution = c.Resolution,
            ResolvedAt = c.ResolvedAt,
            CreatedAt = c.CreatedAt
        };
    }
}
