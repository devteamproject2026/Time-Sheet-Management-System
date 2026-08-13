using System.Collections.Generic;
using System.Threading.Tasks;
using WorkPlus.BusinessService.DTOs;

namespace WorkPlus.BusinessService.Services
{
    public interface IBusinessService
    {
        // Client operations
        Task<ClientResponse> CreateClientAsync(ClientRequest request);
        Task<List<ClientResponse>> GetAllClientsAsync();
        Task<ClientResponse?> GetClientByIdAsync(int id);
        Task<ClientResponse> UpdateClientAsync(int id, ClientRequest request);
        Task DeleteClientAsync(int id);

        // Project operations
        Task<ProjectResponse> CreateProjectAsync(ProjectRequest request, string username);
        Task<List<ProjectResponse>> GetAllProjectsAsync();
        Task<ProjectResponse?> GetProjectByIdAsync(int id);
        Task<List<ProjectResponse>> GetProjectsForManagerAsync(string username);
        Task<List<ProjectResponse>> GetProjectsForEmployeeAsync(string username);
        Task<ProjectResponse> UpdateProjectAsync(int id, ProjectRequest request);
        Task DeleteProjectAsync(int id);

        // Employee-Project Assignment operations
        Task<EmployeeProjectResponse> AssignEmployeeToProjectAsync(EmployeeProjectRequest request);
        Task<List<EmployeeProjectResponse>> GetAllAssignmentsAsync();
        Task<List<EmployeeProjectResponse>> GetMyAssignmentsAsync(string username);
        Task<List<EmployeeProjectResponse>> GetAssignmentsForProjectAsync(int projectId);
        Task<List<EmployeeProjectResponse>> GetAssignmentsForEmployeeAsync(int employeeId);
        Task RemoveEmployeeFromProjectAsync(int assignmentId);
    }
}
