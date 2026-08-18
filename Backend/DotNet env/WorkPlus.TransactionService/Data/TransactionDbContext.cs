using Microsoft.EntityFrameworkCore;
using WorkPlus.TransactionService.Models;

namespace WorkPlus.TransactionService.Data
{
    public class TransactionDbContext : DbContext
    {
        public TransactionDbContext(DbContextOptions<TransactionDbContext> options) : base(options) { }

        public DbSet<TaskItem> Tasks { get; set; } = null!;
        public DbSet<Attendance> Attendances { get; set; } = null!;
        public DbSet<Timesheet> Timesheets { get; set; } = null!;
        public DbSet<TimesheetApproval> TimesheetApprovals { get; set; } = null!;
        public DbSet<Complaint> Complaints { get; set; } = null!;
        public DbSet<UserReference> Users { get; set; } = null!;

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            modelBuilder.Entity<UserReference>(entity =>
            {
                entity.ToTable("users");
                entity.HasKey(e => e.UserId);
                entity.Property(e => e.Role).HasConversion<string>();
            });

            modelBuilder.Entity<TaskItem>(entity =>
            {
                entity.ToTable("tasks");
                entity.HasKey(e => e.TaskId);
                entity.Property(e => e.Status).HasConversion<string>();

                entity.HasOne(t => t.Manager)
                    .WithMany()
                    .HasForeignKey(t => t.ManagerId);

                entity.HasOne(t => t.Employee)
                    .WithMany()
                    .HasForeignKey(t => t.EmployeeId);
            });

            modelBuilder.Entity<Attendance>(entity =>
            {
                entity.ToTable("attendance");
                entity.HasKey(e => e.AttendanceId);
                entity.Property(e => e.Status).HasConversion<string>();
                entity.HasIndex(e => new { e.EmployeeId, e.AttendanceDate }).IsUnique();

                entity.HasOne(a => a.Employee)
                    .WithMany()
                    .HasForeignKey(a => a.EmployeeId);
            });

            modelBuilder.Entity<Timesheet>(entity =>
            {
                entity.ToTable("timesheets");
                entity.HasKey(e => e.TimesheetId);
                entity.Property(e => e.Status).HasConversion<string>();

                entity.HasOne(t => t.Employee)
                    .WithMany()
                    .HasForeignKey(t => t.EmployeeId);

                entity.HasOne(t => t.Task)
                    .WithMany()
                    .HasForeignKey(t => t.TaskId);
            });

            modelBuilder.Entity<TimesheetApproval>(entity =>
            {
                entity.ToTable("timesheet_approvals");
                entity.HasKey(e => e.ApprovalId);
                entity.Property(e => e.ApprovalStatus).HasConversion<string>();

                entity.HasOne(ta => ta.Timesheet)
                    .WithMany()
                    .HasForeignKey(ta => ta.TimesheetId);

                entity.HasOne(ta => ta.Manager)
                    .WithMany()
                    .HasForeignKey(ta => ta.ManagerId);
            });

            modelBuilder.Entity<Complaint>(entity =>
            {
                entity.ToTable("complaints");
                entity.HasKey(e => e.ComplaintId);
                entity.Property(e => e.Status).HasConversion<string>();

                entity.HasOne(c => c.Employee)
                    .WithMany()
                    .HasForeignKey(c => c.EmployeeId);

                entity.HasOne(c => c.Manager)
                    .WithMany()
                    .HasForeignKey(c => c.ManagerId);
            });
        }
    }
}
