using System;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using WorkPlus.TransactionService.DTOs;
using WorkPlus.TransactionService.Services;

namespace WorkPlus.TransactionService.Controllers
{
    [ApiController]
    [Route("api/transactions/attendance")]
    [Authorize]
    public class AttendanceController : ControllerBase
    {
        private readonly ITransactionService _transactionService;

        public AttendanceController(ITransactionService transactionService)
        {
            _transactionService = transactionService;
        }

        [Authorize(Roles = "EMPLOYEE")]
        [HttpPost("check-in")]
        public async Task<IActionResult> CheckIn()
        {
            var username = User.Identity?.Name ?? string.Empty;
            var request = new AttendanceRequest
            {
                AttendanceDate = DateTime.Today,
                CheckIn = DateTime.Now.TimeOfDay,
                Status = "PRESENT"
            };
            var result = await _transactionService.MarkAttendanceAsync(request, username);
            return Ok(result);
        }

        [Authorize(Roles = "EMPLOYEE")]
        [HttpPut("check-out")]
        public async Task<IActionResult> CheckOut()
        {
            var username = User.Identity?.Name ?? string.Empty;
            var request = new AttendanceRequest
            {
                AttendanceDate = DateTime.Today,
                CheckOut = DateTime.Now.TimeOfDay,
                Status = "PRESENT"
            };
            var result = await _transactionService.MarkAttendanceAsync(request, username);
            return Ok(result);
        }

        [Authorize(Roles = "EMPLOYEE")]
        [HttpGet("my")]
        public async Task<IActionResult> MyAttendance()
        {
            var username = User.Identity?.Name ?? string.Empty;
            var logs = await _transactionService.GetAttendanceForEmployeeAsync(username);
            return Ok(logs);
        }

        [Authorize(Roles = "MANAGER")]
        [HttpGet("team")]
        public async Task<IActionResult> TeamAttendance([FromQuery] DateTime? date)
        {
            var username = User.Identity?.Name ?? string.Empty;
            var logs = await _transactionService.GetAttendanceForEmployeeAsync(username);
            return Ok(logs);
        }
    }
}
