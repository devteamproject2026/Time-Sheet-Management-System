using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using WorkPlus.TransactionService.DTOs;
using WorkPlus.TransactionService.Services;

namespace WorkPlus.TransactionService.Controllers
{
    [ApiController]
    [Route("api/transactions/timesheets")]
    [Authorize]
    public class TimesheetController : ControllerBase
    {
        private readonly ITransactionService _transactionService;

        public TimesheetController(ITransactionService transactionService)
        {
            _transactionService = transactionService;
        }

        [Authorize(Roles = "EMPLOYEE")]
        [HttpPost]
        public async Task<IActionResult> SubmitTimesheet([FromBody] TimesheetRequest request)
        {
            var username = User.Identity?.Name ?? string.Empty;
            var result = await _transactionService.SubmitTimesheetAsync(request, username);
            return Ok(result);
        }

        [Authorize(Roles = "EMPLOYEE")]
        [HttpGet("my")]
        public async Task<IActionResult> MyTimesheets()
        {
            var username = User.Identity?.Name ?? string.Empty;
            var result = await _transactionService.GetTimesheetsForEmployeeAsync(username);
            return Ok(result);
        }

        [Authorize(Roles = "MANAGER")]
        [HttpGet("review")]
        public async Task<IActionResult> ReviewTimesheets()
        {
            var username = User.Identity?.Name ?? string.Empty;
            var result = await _transactionService.GetPendingTimesheetsForManagerAsync(username);
            return Ok(result);
        }
    }

    [ApiController]
    [Route("api/transactions/timesheet-approvals")]
    [Authorize(Roles = "MANAGER")]
    public class TimesheetApprovalController : ControllerBase
    {
        private readonly ITransactionService _transactionService;

        public TimesheetApprovalController(ITransactionService transactionService)
        {
            _transactionService = transactionService;
        }

        [HttpPost("timesheet/{timesheetId}")]
        public async Task<IActionResult> ReviewTimesheet(int timesheetId, [FromBody] TimesheetApprovalRequest request)
        {
            var username = User.Identity?.Name ?? string.Empty;
            request.TimesheetId = timesheetId;
            var result = await _transactionService.ProcessTimesheetApprovalAsync(request, username);
            return Ok(result);
        }
    }
}
