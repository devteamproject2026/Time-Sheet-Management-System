using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using WorkPlus.AuthService.Services;

namespace WorkPlus.AuthService.Controllers
{
    [ApiController]
    [Route("api/auth/users")]
    [Authorize(Roles = "HR_HEAD")]
    public class UserLookupController : ControllerBase
    {
        private readonly IAuthService _authService;

        public UserLookupController(IAuthService authService)
        {
            _authService = authService;
        }

        [HttpGet("managers")]
        public async Task<IActionResult> GetActiveManagers()
        {
            var managers = await _authService.GetActiveManagersAsync();
            return Ok(managers);
        }

        [HttpGet("employees")]
        public async Task<IActionResult> GetActiveEmployees()
        {
            var employees = await _authService.GetActiveEmployeesAsync();
            return Ok(employees);
        }
    }
}
