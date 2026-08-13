using Microsoft.EntityFrameworkCore;
using WorkPlus.BusinessService.Models;

namespace WorkPlus.BusinessService.Data
{
    public class BusinessDbContext : DbContext
    {
        public BusinessDbContext(DbContextOptions<BusinessDbContext> options) : base(options) { }

        public DbSet<Client> Clients { get; set; } = null!;
        public DbSet<Project> Projects { get; set; } = null!;
        public DbSet<EmployeeProject> EmployeeProjects { get; set; } = null!;
        public DbSet<User> Users { get; set; } = null!;

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            modelBuilder.Entity<Client>(entity =>
            {
                entity.ToTable("clients");
                entity.HasKey(e => e.ClientId);
            });

            modelBuilder.Entity<User>(entity =>
            {
                entity.ToTable("users");
                entity.HasKey(e => e.UserId);
                entity.Property(e => e.Role).HasConversion<string>();
                entity.Property(e => e.ApprovalStatus).HasConversion<string>();
                entity.Property(e => e.AccountStatus).HasConversion<string>();
            });

            modelBuilder.Entity<Project>(entity =>
            {
                entity.ToTable("projects");
                entity.HasKey(e => e.ProjectId);
                entity.Property(e => e.Status).HasConversion<string>();

                entity.HasOne(p => p.Client)
                    .WithMany()
                    .HasForeignKey(p => p.ClientId);

                entity.HasOne(p => p.Manager)
                    .WithMany()
                    .HasForeignKey(p => p.ManagerId);

                entity.HasOne(p => p.HrHead)
                    .WithMany()
                    .HasForeignKey(p => p.HrHeadId);
            });

            modelBuilder.Entity<EmployeeProject>(entity =>
            {
                entity.ToTable("employee_projects");
                entity.HasKey(e => e.EmployeeProjectId);
                entity.HasIndex(e => new { e.EmployeeId, e.ProjectId }).IsUnique();

                entity.HasOne(ep => ep.Employee)
                    .WithMany()
                    .HasForeignKey(ep => ep.EmployeeId);

                entity.HasOne(ep => ep.Project)
                    .WithMany()
                    .HasForeignKey(ep => ep.ProjectId);
            });
        }
    }
}
