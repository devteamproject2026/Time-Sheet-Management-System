using System;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using WorkPlus.AuthService.DTOs;
using WorkPlus.AuthService.Services;
using WorkPlus.Shared.Enums;

namespace WorkPlus.AuthService.Controllers
{
    [ApiController]
    [Route("api/auth")]
    public class AuthController : ControllerBase
    {
        private readonly IAuthService _authService;

        public AuthController(IAuthService authService)
        {
            _authService = authService;
        }

        [HttpPost("register-hr")]
        public async Task<IActionResult> RegisterHr([FromBody] RegisterHrRequest request)
        {
            try
            {
                var result = await _authService.RegisterUserAsync(request, Role.HR_HEAD, ApprovalStatus.PENDING, AccountStatus.INACTIVE);
                return StatusCode(StatusCodes.Status201Created, result);
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }

        [Authorize(Roles = "HR_HEAD")]
        [HttpPost("register-manager")]
        public async Task<IActionResult> RegisterManager([FromBody] RegisterHrRequest request)
        {
            try
            {
                var result = await _authService.RegisterUserAsync(request, Role.MANAGER, ApprovalStatus.APPROVED, AccountStatus.ACTIVE);
                return StatusCode(StatusCodes.Status201Created, result);
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }

        [Authorize(Roles = "HR_HEAD")]
        [HttpPost("register-employee")]
        public async Task<IActionResult> RegisterEmployee([FromBody] RegisterHrRequest request)
        {
            try
            {
                var result = await _authService.RegisterUserAsync(request, Role.EMPLOYEE, ApprovalStatus.APPROVED, AccountStatus.ACTIVE);
                return StatusCode(StatusCodes.Status201Created, result);
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] LoginRequest request)
        {
            var (user, token) = await _authService.LoginAsync(request);
            if (user == null || token == null)
            {
                return Unauthorized(new { message = "Invalid username or password, or account pending approval." });
            }

            var cookieOptions = new CookieOptions
            {
                HttpOnly = true,
                Secure = false,
                SameSite = SameSiteMode.Lax,
                Expires = DateTime.UtcNow.AddDays(1),
                Path = "/"
            };
            Response.Cookies.Append("jwt", token, cookieOptions);

            var loginResponse = new LoginResponse
            {
                UserId = user.UserId,
                Username = user.Username,
                Email = user.Email,
                Role = user.Role.ToString(),
                FirstName = user.FirstName,
                LastName = user.LastName
            };

            return Ok(loginResponse);
        }

        [Authorize]
        [HttpGet("me")]
        public async Task<IActionResult> GetCurrentUser()
        {
            var username = User.Identity?.Name;
            if (string.IsNullOrEmpty(username)) return Unauthorized();

            var currentUser = await _authService.GetCurrentUserAsync(username);
            return currentUser != null ? Ok(currentUser) : NotFound();
        }

        [Authorize(Roles = "ADMIN")]
        [HttpGet("pending-hr")]
        public async Task<IActionResult> GetPendingHrRequests()
        {
            var requests = await _authService.GetPendingHrRequestsAsync();
            return Ok(requests);
        }

        [Authorize(Roles = "ADMIN")]
        [HttpPut("approve-hr/{id}")]
        public async Task<IActionResult> ApproveHr(int id)
        {
            var result = await _authService.ApproveHrAsync(id);
            return Ok(result);
        }

        [Authorize(Roles = "ADMIN")]
        [HttpPut("reject-hr/{id}")]
        public async Task<IActionResult> RejectHr(int id)
        {
            var result = await _authService.RejectHrAsync(id);
            return Ok(result);
        }

        [HttpPost("logout")]
        public IActionResult Logout()
        {
            Response.Cookies.Delete("jwt", new CookieOptions { Path = "/" });
            return Ok("Logout Successful");
        }

        [HttpPost("change-password")]
        public async Task<IActionResult> ChangePassword([FromBody] ChangePasswordRequest request)
        {
            try
            {
                await _authService.ChangePasswordAsync(request);
                return Ok("Password changed successfully.");
            }
            catch (UnauthorizedAccessException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }
    }
}
