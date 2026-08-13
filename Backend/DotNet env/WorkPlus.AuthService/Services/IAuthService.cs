using System.Collections.Generic;
using System.Threading.Tasks;
using WorkPlus.AuthService.DTOs;
using WorkPlus.AuthService.Models;
using WorkPlus.Shared.Enums;

namespace WorkPlus.AuthService.Services
{
    public interface IAuthService
    {
        Task<string> RegisterUserAsync(RegisterHrRequest request, Role role, ApprovalStatus approvalStatus, AccountStatus accountStatus);
        Task<(User? User, string? Token)> LoginAsync(LoginRequest request);
        Task<CurrentUserResponse?> GetCurrentUserAsync(string username);
        Task<List<UserLookupResponse>> GetActiveManagersAsync();
        Task<List<UserLookupResponse>> GetActiveEmployeesAsync();
        Task<List<User>> GetPendingHrRequestsAsync();
        Task<string> ApproveHrAsync(int userId);
        Task<string> RejectHrAsync(int userId);
        Task ChangePasswordAsync(ChangePasswordRequest request);
    }
}
