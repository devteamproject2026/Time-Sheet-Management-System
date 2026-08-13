using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using WorkPlus.BusinessService.DTOs;
using WorkPlus.BusinessService.Services;

namespace WorkPlus.BusinessService.Controllers
{
    [ApiController]
    [Route("api/business/projects")]
    [Authorize]
    public class ProjectController : ControllerBase
    {
        private readonly IBusinessService _businessService;

        public ProjectController(IBusinessService businessService)
        {
            _businessService = businessService;
        }

        [Authorize(Roles = "HR_HEAD,ADMIN")]
        [HttpPost]
        public async Task<IActionResult> CreateProject([FromBody] ProjectRequest request)
        {
            var username = User.Identity?.Name ?? string.Empty;
            var project = await _businessService.CreateProjectAsync(request, username);
            return CreatedAtAction(nameof(GetProjectById), new { id = project.ProjectId }, project);
        }

        [Authorize(Roles = "ADMIN,HR_HEAD")]
        [HttpGet]
        public async Task<IActionResult> GetAllProjects()
        {
            var projects = await _businessService.GetAllProjectsAsync();
            return Ok(projects);
        }

        [Authorize(Roles = "MANAGER")]
        [HttpGet("my-managed-projects")]
        public async Task<IActionResult> GetMyManagedProjects()
        {
            var username = User.Identity?.Name ?? string.Empty;
            var projects = await _businessService.GetProjectsForManagerAsync(username);
            return Ok(projects);
        }

        [Authorize(Roles = "EMPLOYEE")]
        [HttpGet("my-assigned-projects")]
        public async Task<IActionResult> GetMyAssignedProjects()
        {
            var username = User.Identity?.Name ?? string.Empty;
            var projects = await _businessService.GetProjectsForEmployeeAsync(username);
            return Ok(projects);
        }

        [Authorize(Roles = "ADMIN,HR_HEAD")]
        [HttpGet("{id:int}")]
        public async Task<IActionResult> GetProjectById(int id)
        {
            var project = await _businessService.GetProjectByIdAsync(id);
            return project != null ? Ok(project) : NotFound();
        }

        [Authorize(Roles = "HR_HEAD,ADMIN")]
        [HttpPut("{id:int}")]
        public async Task<IActionResult> UpdateProject(int id, [FromBody] ProjectRequest request)
        {
            var updated = await _businessService.UpdateProjectAsync(id, request);
            return Ok(updated);
        }

        [Authorize(Roles = "HR_HEAD,ADMIN")]
        [HttpDelete("{id:int}")]
        public async Task<IActionResult> DeleteProject(int id)
        {
            await _businessService.DeleteProjectAsync(id);
            return NoContent();
        }
    }
}
