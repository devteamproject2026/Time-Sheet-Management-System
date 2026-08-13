using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using WorkPlus.Shared.Enums;

namespace WorkPlus.AuthService.Models
{
    [Table("users")]
    public class User
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Column("user_id")]
        public int UserId { get; set; }

        [Required]
        [MaxLength(50)]
        [Column("username")]
        public string Username { get; set; } = string.Empty;

        [Required]
        [MaxLength(255)]
        [Column("password")]
        public string Password { get; set; } = string.Empty;

        [Required]
        [MaxLength(50)]
        [Column("first_name")]
        public string FirstName { get; set; } = string.Empty;

        [Required]
        [MaxLength(50)]
        [Column("last_name")]
        public string LastName { get; set; } = string.Empty;

        [Required]
        [MaxLength(100)]
        [Column("email")]
        public string Email { get; set; } = string.Empty;

        [MaxLength(15)]
        [Column("contact")]
        public string? Contact { get; set; }

        [Required]
        [Column("role")]
        public Role Role { get; set; }

        [Column("approval_status")]
        public ApprovalStatus ApprovalStatus { get; set; } = ApprovalStatus.PENDING;

        [Column("account_status")]
        public AccountStatus AccountStatus { get; set; } = AccountStatus.INACTIVE;

        [Column("joining_date")]
        public DateTime? JoiningDate { get; set; }

        [Column("created_at")]
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    }
}
