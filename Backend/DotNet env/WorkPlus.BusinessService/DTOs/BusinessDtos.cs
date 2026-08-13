using System;
using System.ComponentModel.DataAnnotations;

namespace WorkPlus.BusinessService.DTOs
{
    public class ClientRequest
    {
        [Required, MaxLength(100)]
        public string ClientName { get; set; } = string.Empty;
        public string? CompanyName { get; set; }
        public string? Email { get; set; }
        public string? Contact { get; set; }
        public string? Address { get; set; }
    }

    public class ClientResponse
    {
        public int ClientId { get; set; }
        public string ClientName { get; set; } = string.Empty;
        public string? CompanyName { get; set; }
        public string? Email { get; set; }
        public string? Contact { get; set; }
        public string? Address { get; set; }
        public DateTime CreatedAt { get; set; }
    }

    public class ProjectRequest
    {
        [Required, MaxLength(100)]
        public string ProjectName { get; set; } = string.Empty;
        public string? Description { get; set; }
        public int ClientId { get; set; }
        public int ManagerId { get; set; }
        public DateTime? StartDate { get; set; }
        public DateTime? EndDate { get; set; }
        public string Status { get; set; } = "ACTIVE";
    }

    public class ProjectResponse
    {
        public int ProjectId { get; set; }
        public string ProjectName { get; set; } = string.Empty;
        public string? Description { get; set; }
        public int ClientId { get; set; }
        public string ClientName { get; set; } = string.Empty;
        public int ManagerId { get; set; }
        public string ManagerName { get; set; } = string.Empty;
        public string ManagerUsername { get; set; } = string.Empty;
        public int HrHeadId { get; set; }
        public string HrHeadName { get; set; } = string.Empty;
        public string HrHeadUsername { get; set; } = string.Empty;
        public DateTime? StartDate { get; set; }
        public DateTime? EndDate { get; set; }
        public string Status { get; set; } = "ACTIVE";
        public DateTime CreatedAt { get; set; }
    }

    public class EmployeeProjectRequest
    {
        public int EmployeeId { get; set; }
        public int ProjectId { get; set; }
    }

    public class EmployeeProjectResponse
    {
        public int EmployeeProjectId { get; set; }
        public int EmployeeId { get; set; }
        public string EmployeeUsername { get; set; } = string.Empty;
        public string EmployeeFullName { get; set; } = string.Empty;
        public string EmployeeName { get; set; } = string.Empty;
        public int ProjectId { get; set; }
        public string ProjectName { get; set; } = string.Empty;
        public int ManagerId { get; set; }
        public string ManagerUsername { get; set; } = string.Empty;
        public DateTime AssignedDate { get; set; }
    }
}
