using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using WorkPlus.TransactionService.Services;

namespace WorkPlus.TransactionService.Controllers
{
    [ApiController]
    [Route("api/transactions/reports/employees")]
    [Authorize(Roles = "MANAGER")]
    public class EmployeeReportController : ControllerBase
    {
        private readonly ITransactionService _transactionService;

        public EmployeeReportController(ITransactionService transactionService)
        {
            _transactionService = transactionService;
        }

        [HttpGet]
        public async Task<IActionResult> GetAllEmployeeReports()
        {
            var managerUsername = User.Identity?.Name ?? string.Empty;
            var reports = await _transactionService.GetEmployeeReportsForManagerAsync(managerUsername);
            return Ok(reports);
        }

        [HttpGet("{employeeId}")]
        public async Task<IActionResult> GetEmployeeReport(int employeeId)
        {
            var managerUsername = User.Identity?.Name ?? string.Empty;
            var report = await _transactionService.GetEmployeeReportForManagerAsync(managerUsername, employeeId);
            return report != null ? Ok(report) : NotFound();
        }
    }
}
