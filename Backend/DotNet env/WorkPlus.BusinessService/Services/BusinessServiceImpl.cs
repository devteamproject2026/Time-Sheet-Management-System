using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using WorkPlus.BusinessService.Data;
using WorkPlus.BusinessService.DTOs;
using WorkPlus.BusinessService.Models;
using WorkPlus.Shared.Enums;

namespace WorkPlus.BusinessService.Services
{
    public class BusinessServiceImpl : IBusinessService
    {
        private readonly BusinessDbContext _context;

        public BusinessServiceImpl(BusinessDbContext context)
        {
            _context = context;
        }

        // --- Client Operations ---
        public async Task<ClientResponse> CreateClientAsync(ClientRequest request)
        {
            var client = new Client
            {
                ClientName = request.ClientName,
                CompanyName = request.CompanyName,
                Email = request.Email,
                Contact = request.Contact,
                Address = request.Address,
                CreatedAt = DateTime.UtcNow
            };
            _context.Clients.Add(client);
            await _context.SaveChangesAsync();
            return MapClientToResponse(client);
        }

        public async Task<List<ClientResponse>> GetAllClientsAsync()
        {
            var clients = await _context.Clients.ToListAsync();
            return clients.Select(MapClientToResponse).ToList();
        }

        public async Task<ClientResponse?> GetClientByIdAsync(int id)
        {
            var client = await _context.Clients.FindAsync(id);
            return client != null ? MapClientToResponse(client) : null;
        }

        public async Task<ClientResponse> UpdateClientAsync(int id, ClientRequest request)
        {
            var client = await _context.Clients.FindAsync(id);
            if (client == null) throw new KeyNotFoundException("Client not found");

            client.ClientName = request.ClientName;
            client.CompanyName = request.CompanyName;
            client.Email = request.Email;
            client.Contact = request.Contact;
            client.Address = request.Address;

            await _context.SaveChangesAsync();
            return MapClientToResponse(client);
        }

        public async Task DeleteClientAsync(int id)
        {
            var client = await _context.Clients.FindAsync(id);
            if (client != null)
            {
                _context.Clients.Remove(client);
                await _context.SaveChangesAsync();
            }
        }

        // --- Project Operations ---
        public async Task<ProjectResponse> CreateProjectAsync(ProjectRequest request, string username)
        {
            var hrUser = await _context.Users.FirstOrDefaultAsync(u => u.Username == username);
            if (hrUser == null) throw new UnauthorizedAccessException("HR User not found");

            var manager = await _context.Users.FindAsync(request.ManagerId);
            if (manager == null || manager.Role != Role.MANAGER)
                throw new InvalidOperationException("Invalid Manager ID");

            var client = await _context.Clients.FindAsync(request.ClientId);
            if (client == null) throw new InvalidOperationException("Invalid Client ID");

            Enum.TryParse<ProjectStatus>(request.Status, true, out var status);

            var project = new Project
            {
                ProjectName = request.ProjectName,
                Description = request.Description,
                ClientId = request.ClientId,
                ManagerId = request.ManagerId,
                HrHeadId = hrUser.UserId,
                StartDate = request.StartDate,
                EndDate = request.EndDate,
                Status = status,
                CreatedAt = DateTime.UtcNow
            };

            _context.Projects.Add(project);
            await _context.SaveChangesAsync();

            return await GetProjectByIdAsync(project.ProjectId) ?? throw new Exception("Project creation failed");
        }

        public async Task<List<ProjectResponse>> GetAllProjectsAsync()
        {
            return await _context.Projects
                .Include(p => p.Client)
                .Include(p => p.Manager)
                .Include(p => p.HrHead)
                .Select(p => MapProjectToResponse(p))
                .ToListAsync();
        }

        public async Task<ProjectResponse?> GetProjectByIdAsync(int id)
        {
            var p = await _context.Projects
                .Include(p => p.Client)
                .Include(p => p.Manager)
                .Include(p => p.HrHead)
                .FirstOrDefaultAsync(x => x.ProjectId == id);

            return p != null ? MapProjectToResponse(p) : null;
        }

        public async Task<List<ProjectResponse>> GetProjectsForManagerAsync(string username)
        {
            var manager = await _context.Users.FirstOrDefaultAsync(u => u.Username == username);
            if (manager == null) return new List<ProjectResponse>();

            return await _context.Projects
                .Include(p => p.Client)
                .Include(p => p.Manager)
                .Include(p => p.HrHead)
                .Where(p => p.ManagerId == manager.UserId)
                .Select(p => MapProjectToResponse(p))
                .ToListAsync();
        }

        public async Task<List<ProjectResponse>> GetProjectsForEmployeeAsync(string username)
        {
            var employee = await _context.Users.FirstOrDefaultAsync(u => u.Username == username);
            if (employee == null) return new List<ProjectResponse>();

            var assignedProjectIds = _context.EmployeeProjects
                .Where(ep => ep.EmployeeId == employee.UserId)
                .Select(ep => ep.ProjectId);

            return await _context.Projects
                .Include(p => p.Client)
                .Include(p => p.Manager)
                .Include(p => p.HrHead)
                .Where(p => assignedProjectIds.Contains(p.ProjectId))
                .Select(p => MapProjectToResponse(p))
                .ToListAsync();
        }

        public async Task<ProjectResponse> UpdateProjectAsync(int id, ProjectRequest request)
        {
            var project = await _context.Projects.FindAsync(id);
            if (project == null) throw new KeyNotFoundException("Project not found");

            Enum.TryParse<ProjectStatus>(request.Status, true, out var status);

            project.ProjectName = request.ProjectName;
            project.Description = request.Description;
            project.ClientId = request.ClientId;
            project.ManagerId = request.ManagerId;
            project.StartDate = request.StartDate;
            project.EndDate = request.EndDate;
            project.Status = status;

            await _context.SaveChangesAsync();
            return await GetProjectByIdAsync(id) ?? throw new Exception("Update failed");
        }

        public async Task DeleteProjectAsync(int id)
        {
            var project = await _context.Projects.FindAsync(id);
            if (project != null)
            {
                _context.Projects.Remove(project);
                await _context.SaveChangesAsync();
            }
        }

        // --- Employee-Project Assignment Operations ---
        public async Task<EmployeeProjectResponse> AssignEmployeeToProjectAsync(EmployeeProjectRequest request)
        {
            var exists = await _context.EmployeeProjects.AnyAsync(ep => ep.EmployeeId == request.EmployeeId && ep.ProjectId == request.ProjectId);
            if (exists) throw new InvalidOperationException("Employee already assigned to this project");

            var ep = new EmployeeProject
            {
                EmployeeId = request.EmployeeId,
                ProjectId = request.ProjectId,
                AssignedDate = DateTime.UtcNow
            };

            _context.EmployeeProjects.Add(ep);
            await _context.SaveChangesAsync();

            var saved = await _context.EmployeeProjects
                .Include(x => x.Employee)
                .Include(x => x.Project)
                .ThenInclude(p => p!.Manager)
                .FirstAsync(x => x.EmployeeProjectId == ep.EmployeeProjectId);

            return MapEmployeeProjectToResponse(saved);
        }

        public async Task<List<EmployeeProjectResponse>> GetAllAssignmentsAsync()
        {
            return await _context.EmployeeProjects
                .Include(ep => ep.Employee)
                .Include(ep => ep.Project)
                .ThenInclude(p => p!.Manager)
                .Select(ep => MapEmployeeProjectToResponse(ep))
                .ToListAsync();
        }

        public async Task<List<EmployeeProjectResponse>> GetMyAssignmentsAsync(string username)
        {
            var emp = await _context.Users.FirstOrDefaultAsync(u => u.Username == username);
            if (emp == null) return new List<EmployeeProjectResponse>();

            return await _context.EmployeeProjects
                .Include(ep => ep.Employee)
                .Include(ep => ep.Project)
                .ThenInclude(p => p!.Manager)
                .Where(ep => ep.EmployeeId == emp.UserId)
                .Select(ep => MapEmployeeProjectToResponse(ep))
                .ToListAsync();
        }

        public async Task<List<EmployeeProjectResponse>> GetAssignmentsForProjectAsync(int projectId)
        {
            return await _context.EmployeeProjects
                .Include(ep => ep.Employee)
                .Include(ep => ep.Project)
                .ThenInclude(p => p!.Manager)
                .Where(ep => ep.ProjectId == projectId)
                .Select(ep => MapEmployeeProjectToResponse(ep))
                .ToListAsync();
        }

        public async Task<List<EmployeeProjectResponse>> GetAssignmentsForEmployeeAsync(int employeeId)
        {
            return await _context.EmployeeProjects
                .Include(ep => ep.Employee)
                .Include(ep => ep.Project)
                .ThenInclude(p => p!.Manager)
                .Where(ep => ep.EmployeeId == employeeId)
                .Select(ep => MapEmployeeProjectToResponse(ep))
                .ToListAsync();
        }

        public async Task RemoveEmployeeFromProjectAsync(int assignmentId)
        {
            var ep = await _context.EmployeeProjects.FindAsync(assignmentId);
            if (ep != null)
            {
                _context.EmployeeProjects.Remove(ep);
                await _context.SaveChangesAsync();
            }
        }

        // Mappers
        private static ClientResponse MapClientToResponse(Client c) => new()
        {
            ClientId = c.ClientId,
            ClientName = c.ClientName,
            CompanyName = c.CompanyName,
            Email = c.Email,
            Contact = c.Contact,
            Address = c.Address,
            CreatedAt = c.CreatedAt
        };

        private static ProjectResponse MapProjectToResponse(Project p) => new()
        {
            ProjectId = p.ProjectId,
            ProjectName = p.ProjectName,
            Description = p.Description,
            ClientId = p.ClientId,
            ClientName = p.Client?.ClientName ?? string.Empty,
            ManagerId = p.ManagerId,
            ManagerName = p.Manager != null ? $"{p.Manager.FirstName} {p.Manager.LastName}" : string.Empty,
            ManagerUsername = p.Manager?.Username ?? string.Empty,
            HrHeadId = p.HrHeadId,
            HrHeadName = p.HrHead != null ? $"{p.HrHead.FirstName} {p.HrHead.LastName}" : string.Empty,
            HrHeadUsername = p.HrHead?.Username ?? string.Empty,
            StartDate = p.StartDate,
            EndDate = p.EndDate,
            Status = p.Status.ToString(),
            CreatedAt = p.CreatedAt
        };

        private static EmployeeProjectResponse MapEmployeeProjectToResponse(EmployeeProject ep) => new()
        {
            EmployeeProjectId = ep.EmployeeProjectId,
            EmployeeId = ep.EmployeeId,
            EmployeeUsername = ep.Employee?.Username ?? string.Empty,
            EmployeeFullName = ep.Employee != null ? $"{ep.Employee.FirstName} {ep.Employee.LastName}" : string.Empty,
            EmployeeName = ep.Employee != null ? $"{ep.Employee.FirstName} {ep.Employee.LastName}" : string.Empty,
            ProjectId = ep.ProjectId,
            ProjectName = ep.Project?.ProjectName ?? string.Empty,
            ManagerId = ep.Project?.ManagerId ?? 0,
            ManagerUsername = ep.Project?.Manager?.Username ?? string.Empty,
            AssignedDate = ep.AssignedDate
        };
    }
}
