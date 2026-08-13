using Microsoft.EntityFrameworkCore;
using WorkPlus.AuthService.Models;
using WorkPlus.Shared.Enums;

namespace WorkPlus.AuthService.Data
{
    public class AuthDbContext : DbContext
    {
        public AuthDbContext(DbContextOptions<AuthDbContext> options) : base(options) { }

        public DbSet<User> Users { get; set; } = null!;

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            modelBuilder.Entity<User>(entity =>
            {
                entity.ToTable("users");
                entity.HasKey(e => e.UserId);

                entity.Property(e => e.Role)
                    .HasConversion<string>();

                entity.Property(e => e.ApprovalStatus)
                    .HasConversion<string>();

                entity.Property(e => e.AccountStatus)
                    .HasConversion<string>();
            });
        }
    }
}
