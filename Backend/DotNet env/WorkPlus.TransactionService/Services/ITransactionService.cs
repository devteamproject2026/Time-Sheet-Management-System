using System.Collections.Generic;
using System.Threading.Tasks;
using WorkPlus.TransactionService.DTOs;

namespace WorkPlus.TransactionService.Services
{
    public interface ITransactionService
    {
        // Tasks
        Task<TaskResponse> CreateTaskAsync(TaskRequest request, string managerUsername);
        Task<List<TaskResponse>> GetTasksForEmployeeAsync(string employeeUsername);
        Task<List<TaskResponse>> GetTasksForManagerAsync(string managerUsername);
        Task<TaskResponse> UpdateTaskStatusAsync(int taskId, TaskStatusUpdateRequest request, string username);

        // Attendance
        Task<AttendanceResponse> MarkAttendanceAsync(AttendanceRequest request, string username);
        Task<List<AttendanceResponse>> GetAttendanceForEmployeeAsync(string username);

        // Timesheets
        Task<TimesheetResponse> SubmitTimesheetAsync(TimesheetRequest request, string employeeUsername);
        Task<List<TimesheetResponse>> GetTimesheetsForEmployeeAsync(string employeeUsername);
        Task<List<TimesheetResponse>> GetPendingTimesheetsForManagerAsync(string managerUsername);
        Task<string> ProcessTimesheetApprovalAsync(TimesheetApprovalRequest request, string managerUsername);

        // Complaints
        Task<ComplaintResponse> CreateComplaintAsync(ComplaintRequest request, string employeeUsername);
        Task<List<ComplaintResponse>> GetComplaintsForEmployeeAsync(string employeeUsername);
        Task<List<ComplaintResponse>> GetAllComplaintsForAdminAsync();
        Task<ComplaintResponse> ResolveComplaintAsync(int complaintId, ResolveComplaintRequest request, string managerUsername);

        // Reports
        Task<List<EmployeeReportResponse>> GetEmployeeReportsForManagerAsync(string managerUsername);
        Task<EmployeeReportResponse?> GetEmployeeReportForManagerAsync(string managerUsername, int employeeId);
    }
}
