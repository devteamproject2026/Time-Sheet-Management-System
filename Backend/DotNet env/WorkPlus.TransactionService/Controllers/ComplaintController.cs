using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using WorkPlus.TransactionService.DTOs;
using WorkPlus.TransactionService.Services;

namespace WorkPlus.TransactionService.Controllers
{
    [ApiController]
    [Route("api/transactions/complaints")]
    [Authorize]
    public class ComplaintController : ControllerBase
    {
        private readonly ITransactionService _transactionService;

        public ComplaintController(ITransactionService transactionService)
        {
            _transactionService = transactionService;
        }

        [Authorize(Roles = "EMPLOYEE")]
        [HttpPost]
        public async Task<IActionResult> RaiseComplaint([FromBody] ComplaintRequest request)
        {
            var username = User.Identity?.Name ?? string.Empty;
            var complaint = await _transactionService.CreateComplaintAsync(request, username);
            return Ok(complaint);
        }

        [Authorize(Roles = "EMPLOYEE")]
        [HttpGet("my")]
        public async Task<IActionResult> MyComplaints()
        {
            var username = User.Identity?.Name ?? string.Empty;
            var complaints = await _transactionService.GetComplaintsForEmployeeAsync(username);
            return Ok(complaints);
        }

        [Authorize(Roles = "MANAGER")]
        [HttpGet("assigned")]
        public async Task<IActionResult> AssignedComplaints()
        {
            var username = User.Identity?.Name ?? string.Empty;
            var complaints = await _transactionService.GetComplaintsForEmployeeAsync(username);
            return Ok(complaints);
        }

        [Authorize(Roles = "MANAGER")]
        [HttpPut("{complaintId}/resolve")]
        public async Task<IActionResult> ResolveComplaint(int complaintId, [FromBody] ResolveComplaintRequest request)
        {
            var username = User.Identity?.Name ?? string.Empty;
            var result = await _transactionService.ResolveComplaintAsync(complaintId, request, username);
            return Ok(result);
        }

        [Authorize(Roles = "EMPLOYEE")]
        [HttpGet("available-managers")]
        public async Task<IActionResult> AvailableManagers()
        {
            return Ok(new object[] { });
        }
    }
}
