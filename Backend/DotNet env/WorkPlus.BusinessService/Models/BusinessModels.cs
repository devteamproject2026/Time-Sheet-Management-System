using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using WorkPlus.Shared.Enums;

namespace WorkPlus.BusinessService.Models
{
    [Table("clients")]
    public class Client
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Column("client_id")]
        public int ClientId { get; set; }

        [Required, MaxLength(100)]
        [Column("client_name")]
        public string ClientName { get; set; } = string.Empty;

        [MaxLength(100)]
        [Column("company_name")]
        public string? CompanyName { get; set; }

        [MaxLength(100)]
        [Column("email")]
        public string? Email { get; set; }

        [MaxLength(15)]
        [Column("contact")]
        public string? Contact { get; set; }

        [MaxLength(255)]
        [Column("address")]
        public string? Address { get; set; }

        [Column("created_at")]
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    }

    [Table("users")]
    public class User
    {
        [Key]
        [Column("user_id")]
        public int UserId { get; set; }

        [Column("username")]
        public string Username { get; set; } = string.Empty;

        [Column("first_name")]
        public string FirstName { get; set; } = string.Empty;

        [Column("last_name")]
        public string LastName { get; set; } = string.Empty;

        [Column("email")]
        public string Email { get; set; } = string.Empty;

        [Column("role")]
        public Role Role { get; set; }

        [Column("approval_status")]
        public ApprovalStatus ApprovalStatus { get; set; }

        [Column("account_status")]
        public AccountStatus AccountStatus { get; set; }
    }

    [Table("projects")]
    public class Project
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Column("project_id")]
        public int ProjectId { get; set; }

        [Required, MaxLength(100)]
        [Column("project_name")]
        public string ProjectName { get; set; } = string.Empty;

        [Column("description", TypeName = "TEXT")]
        public string? Description { get; set; }

        [Column("client_id")]
        public int ClientId { get; set; }

        [ForeignKey("ClientId")]
        public virtual Client? Client { get; set; }

        [Column("manager_id")]
        public int ManagerId { get; set; }

        [ForeignKey("ManagerId")]
        public virtual User? Manager { get; set; }

        [Column("hr_head_id")]
        public int HrHeadId { get; set; }

        [ForeignKey("HrHeadId")]
        public virtual User? HrHead { get; set; }

        [Column("start_date")]
        public DateTime? StartDate { get; set; }

        [Column("end_date")]
        public DateTime? EndDate { get; set; }

        [Column("status")]
        public ProjectStatus Status { get; set; } = ProjectStatus.ACTIVE;

        [Column("created_at")]
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    }

    [Table("employee_projects")]
    public class EmployeeProject
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Column("employee_project_id")]
        public int EmployeeProjectId { get; set; }

        [Column("employee_id")]
        public int EmployeeId { get; set; }

        [ForeignKey("EmployeeId")]
        public virtual User? Employee { get; set; }

        [Column("project_id")]
        public int ProjectId { get; set; }

        [ForeignKey("ProjectId")]
        public virtual Project? Project { get; set; }

        [Column("assigned_date")]
        public DateTime AssignedDate { get; set; } = DateTime.UtcNow;
    }
}
