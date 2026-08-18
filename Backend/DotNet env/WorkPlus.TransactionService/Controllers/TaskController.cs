using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using WorkPlus.TransactionService.DTOs;
using WorkPlus.TransactionService.Services;

namespace WorkPlus.TransactionService.Controllers
{
    [ApiController]
    [Route("api/transactions/tasks")]
    [Authorize]
    public class TaskController : ControllerBase
    {
        private readonly ITransactionService _transactionService;

        public TaskController(ITransactionService transactionService)
        {
            _transactionService = transactionService;
        }

        [Authorize(Roles = "MANAGER")]
        [HttpPost]
        public async Task<IActionResult> CreateTask([FromBody] TaskRequest request)
        {
            var managerUsername = User.Identity?.Name ?? string.Empty;
            var task = await _transactionService.CreateTaskAsync(request, managerUsername);
            return Ok(task);
        }

        [Authorize(Roles = "EMPLOYEE")]
        [HttpGet("my")]
        public async Task<IActionResult> GetMyTasks()
        {
            var employeeUsername = User.Identity?.Name ?? string.Empty;
            var tasks = await _transactionService.GetTasksForEmployeeAsync(employeeUsername);
            return Ok(tasks);
        }

        [Authorize(Roles = "MANAGER")]
        [HttpGet("managed")]
        public async Task<IActionResult> GetManagedTasks()
        {
            var managerUsername = User.Identity?.Name ?? string.Empty;
            var tasks = await _transactionService.GetTasksForManagerAsync(managerUsername);
            return Ok(tasks);
        }

        [Authorize(Roles = "EMPLOYEE")]
        [HttpPut("{taskId}/accept")]
        public async Task<IActionResult> AcceptTask(int taskId)
        {
            var username = User.Identity?.Name ?? string.Empty;
            var updateRequest = new TaskStatusUpdateRequest { Status = "ACCEPTED", ProgressPercent = 0 };
            var updated = await _transactionService.UpdateTaskStatusAsync(taskId, updateRequest, username);
            return Ok(updated);
        }

        [Authorize(Roles = "EMPLOYEE")]
        [HttpPut("{taskId}/progress")]
        public async Task<IActionResult> UpdateProgress(int taskId, [FromBody] TaskStatusUpdateRequest request)
        {
            var username = User.Identity?.Name ?? string.Empty;
            var updated = await _transactionService.UpdateTaskStatusAsync(taskId, request, username);
            return Ok(updated);
        }

        [Authorize(Roles = "MANAGER")]
        [HttpPut("{taskId}")]
        public async Task<IActionResult> UpdateTask(int taskId, [FromBody] TaskStatusUpdateRequest request)
        {
            var username = User.Identity?.Name ?? string.Empty;
            var updated = await _transactionService.UpdateTaskStatusAsync(taskId, request, username);
            return Ok(updated);
        }
    }
}
