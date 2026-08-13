using System;
using System.Text;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Builder;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.IdentityModel.Tokens;
using WorkPlus.AuthService.Data;
using WorkPlus.AuthService.Services;
using WorkPlus.Shared.Security;

var builder = WebApplication.CreateBuilder(args);

// Configure port to 8081
builder.WebHost.UseUrls("http://localhost:8081");

// DB Connection
var connectionString = builder.Configuration.GetConnectionString("DefaultConnection") 
    ?? "Server=localhost;Database=TimeSheetDB;User=root;Password=root;";

builder.Services.AddDbContext<AuthDbContext>(options =>
    options.UseMySql(connectionString, ServerVersion.AutoDetect(connectionString))
           .UseSnakeCaseNamingConvention());

// Services
builder.Services.AddScoped<IAuthService, AuthServiceImpl>();

// JWT Authentication
var secretKey = builder.Configuration["Jwt:Secret"] ?? JwtTokenHelper.DefaultSecretKey;
var keyBytes = Convert.FromBase64String(secretKey);

builder.Services.AddAuthentication(options =>
{
    options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
})
.AddJwtBearer(options =>
{
    options.RequireHttpsMetadata = false;
    options.SaveToken = true;
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuerSigningKey = true,
        IssuerSigningKey = new SymmetricSecurityKey(keyBytes),
        ValidateIssuer = false,
        ValidateAudience = false,
        ClockSkew = TimeSpan.Zero
    };

    // Extract token from HttpOnly cookie 'jwt' if Bearer header is missing
    options.Events = new JwtBearerEvents
    {
        OnMessageReceived = context =>
        {
            if (context.Request.Cookies.TryGetValue("jwt", out var token))
            {
                context.Token = token;
            }
            return Task.CompletedTask;
        }
    };
});

builder.Services.AddAuthorization();
builder.Services.AddControllers();

// CORS
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowFrontend", policy =>
    {
        policy.WithOrigins("http://localhost:5173", "http://localhost:8080")
              .AllowAnyHeader()
              .AllowAnyMethod()
              .AllowCredentials();
    });
});

var app = builder.Build();

app.UseCors("AllowFrontend");
app.UseAuthentication();
app.UseAuthorization();
app.MapControllers();

app.Run();
