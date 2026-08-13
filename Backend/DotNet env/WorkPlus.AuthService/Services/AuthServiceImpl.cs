using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using BCrypt.Net;
using Microsoft.EntityFrameworkCore;
using WorkPlus.AuthService.Data;
using WorkPlus.AuthService.DTOs;
using WorkPlus.AuthService.Models;
using WorkPlus.Shared.Enums;
using WorkPlus.Shared.Security;

namespace WorkPlus.AuthService.Services
{
    public class AuthServiceImpl : IAuthService
    {
        private readonly AuthDbContext _context;

        public AuthServiceImpl(AuthDbContext context)
        {
            _context = context;
        }

        public async Task<string> RegisterUserAsync(RegisterHrRequest request, Role role, ApprovalStatus approvalStatus, AccountStatus accountStatus)
        {
            if (await _context.Users.AnyAsync(u => u.Username == request.Username))
            {
                throw new InvalidOperationException("Username is already taken");
            }

            if (await _context.Users.AnyAsync(u => u.Email == request.Email))
            {
                throw new InvalidOperationException("Email is already registered");
            }

            var user = new User
            {
                Username = request.Username,
                Password = BCrypt.Net.BCrypt.HashPassword(request.Password),
                FirstName = request.FirstName,
                LastName = request.LastName,
                Email = request.Email,
                Contact = request.Contact,
                Role = role,
                ApprovalStatus = approvalStatus,
                AccountStatus = accountStatus,
                JoiningDate = request.JoiningDate,
                CreatedAt = DateTime.UtcNow
            };

            _context.Users.Add(user);
            await _context.SaveChangesAsync();

            return role == Role.HR_HEAD && approvalStatus == ApprovalStatus.PENDING
                ? "HR registration submitted successfully. Awaiting Admin approval."
                : "User registered successfully.";
        }

        public async Task<(User? User, string? Token)> LoginAsync(LoginRequest request)
        {
            var user = await _context.Users.FirstOrDefaultAsync(u => u.Username == request.Username);
            if (user == null)
            {
                return (null, null);
            }

            bool isPasswordValid = false;
            if (user.Password.StartsWith("$2a$") || user.Password.StartsWith("$2b$") || user.Password.StartsWith("$2y$"))
            {
                try
                {
                    isPasswordValid = BCrypt.Net.BCrypt.Verify(request.Password, user.Password);
                }
                catch
                {
                    isPasswordValid = false;
                }
            }
            else
            {
                // Fallback for sample DB seed plain-text passwords
                isPasswordValid = request.Password == user.Password;
            }

            if (!isPasswordValid)
            {
                return (null, null);
            }

            if (user.ApprovalStatus != ApprovalStatus.APPROVED || user.AccountStatus != AccountStatus.ACTIVE)
            {
                return (null, null);
            }

            var token = JwtTokenHelper.GenerateToken(user.Username, user.Role.ToString());
            return (user, token);
        }

        public async Task<CurrentUserResponse?> GetCurrentUserAsync(string username)
        {
            var user = await _context.Users.FirstOrDefaultAsync(u => u.Username == username);
            if (user == null) return null;

            return new CurrentUserResponse
            {
                UserId = user.UserId,
                Username = user.Username,
                Email = user.Email,
                Role = user.Role.ToString(),
                FirstName = user.FirstName,
                LastName = user.LastName,
                ApprovalStatus = user.ApprovalStatus.ToString(),
                AccountStatus = user.AccountStatus.ToString()
            };
        }

        public async Task<List<UserLookupResponse>> GetActiveManagersAsync()
        {
            return await _context.Users
                .Where(u => u.Role == Role.MANAGER && u.ApprovalStatus == ApprovalStatus.APPROVED && u.AccountStatus == AccountStatus.ACTIVE)
                .Select(u => new UserLookupResponse
                {
                    UserId = u.UserId,
                    Username = u.Username,
                    FullName = $"{u.FirstName} {u.LastName}",
                    Email = u.Email,
                    Role = u.Role.ToString()
                }).ToListAsync();
        }

        public async Task<List<UserLookupResponse>> GetActiveEmployeesAsync()
        {
            return await _context.Users
                .Where(u => u.Role == Role.EMPLOYEE && u.ApprovalStatus == ApprovalStatus.APPROVED && u.AccountStatus == AccountStatus.ACTIVE)
                .Select(u => new UserLookupResponse
                {
                    UserId = u.UserId,
                    Username = u.Username,
                    FullName = $"{u.FirstName} {u.LastName}",
                    Email = u.Email,
                    Role = u.Role.ToString()
                }).ToListAsync();
        }

        public async Task<List<User>> GetPendingHrRequestsAsync()
        {
            return await _context.Users
                .Where(u => u.Role == Role.HR_HEAD && u.ApprovalStatus == ApprovalStatus.PENDING)
                .ToListAsync();
        }

        public async Task<string> ApproveHrAsync(int userId)
        {
            var user = await _context.Users.FindAsync(userId);
            if (user == null) throw new KeyNotFoundException("User not found");

            user.ApprovalStatus = ApprovalStatus.APPROVED;
            user.AccountStatus = AccountStatus.ACTIVE;
            await _context.SaveChangesAsync();
            return "HR approved successfully.";
        }

        public async Task<string> RejectHrAsync(int userId)
        {
            var user = await _context.Users.FindAsync(userId);
            if (user == null) throw new KeyNotFoundException("User not found");

            user.ApprovalStatus = ApprovalStatus.REJECTED;
            user.AccountStatus = AccountStatus.INACTIVE;
            await _context.SaveChangesAsync();
            return "HR rejected successfully.";
        }

        public async Task ChangePasswordAsync(ChangePasswordRequest request)
        {
            var user = await _context.Users.FirstOrDefaultAsync(u => u.Username == request.Username);
            if (user == null) throw new UnauthorizedAccessException("Invalid credentials");

            bool isOldValid = false;
            if (user.Password.StartsWith("$2a$") || user.Password.StartsWith("$2b$") || user.Password.StartsWith("$2y$"))
            {
                isOldValid = BCrypt.Net.BCrypt.Verify(request.OldPassword, user.Password);
            }
            else
            {
                isOldValid = request.OldPassword == user.Password;
            }

            if (!isOldValid) throw new UnauthorizedAccessException("Invalid credentials");

            user.Password = BCrypt.Net.BCrypt.HashPassword(request.NewPassword);
            await _context.SaveChangesAsync();
        }
    }
}
