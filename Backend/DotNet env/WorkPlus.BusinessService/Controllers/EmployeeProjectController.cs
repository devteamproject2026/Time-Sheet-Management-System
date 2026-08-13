using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using WorkPlus.BusinessService.DTOs;
using WorkPlus.BusinessService.Services;

namespace WorkPlus.BusinessService.Controllers
{
    [ApiController]
    [Route("api/business/employee-projects")]
    [Authorize]
    public class EmployeeProjectController : ControllerBase
    {
        private readonly IBusinessService _businessService;

        public EmployeeProjectController(IBusinessService businessService)
        {
            _businessService = businessService;
        }

        [Authorize(Roles = "HR_HEAD,ADMIN")]
        [HttpPost]
        public async Task<IActionResult> AssignEmployeeToProject([FromBody] EmployeeProjectRequest request)
        {
            var assignment = await _businessService.AssignEmployeeToProjectAsync(request);
            return Ok(assignment);
        }

        [Authorize(Roles = "HR_HEAD")]
        [HttpGet]
        public async Task<IActionResult> GetAllAssignments()
        {
            var assignments = await _businessService.GetAllAssignmentsAsync();
            return Ok(assignments);
        }

        [Authorize(Roles = "EMPLOYEE")]
        [HttpGet("my-projects")]
        public async Task<IActionResult> GetMyAssignments()
        {
            var username = User.Identity?.Name ?? string.Empty;
            var assignments = await _businessService.GetMyAssignmentsAsync(username);
            return Ok(assignments);
        }

        [HttpGet("project/{projectId}")]
        public async Task<IActionResult> GetAssignmentsForProject(int projectId)
        {
            var assignments = await _businessService.GetAssignmentsForProjectAsync(projectId);
            return Ok(assignments);
        }

        [HttpGet("employee/{employeeId}")]
        public async Task<IActionResult> GetAssignmentsForEmployee(int employeeId)
        {
            var assignments = await _businessService.GetAssignmentsForEmployeeAsync(employeeId);
            return Ok(assignments);
        }

        [Authorize(Roles = "HR_HEAD,ADMIN")]
        [HttpDelete("{id}")]
        public async Task<IActionResult> RemoveAssignment(int id)
        {
            await _businessService.RemoveEmployeeFromProjectAsync(id);
            return NoContent();
        }
    }
}
