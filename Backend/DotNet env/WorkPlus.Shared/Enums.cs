namespace WorkPlus.Shared.Enums
{
    public enum Role
    {
        ADMIN,
        HR_HEAD,
        MANAGER,
        EMPLOYEE
    }

    public enum ApprovalStatus
    {
        PENDING,
        APPROVED,
        REJECTED
    }

    public enum AccountStatus
    {
        ACTIVE,
        INACTIVE
    }

    public enum ProjectStatus
    {
        ACTIVE,
        COMPLETED,
        ON_HOLD
    }

    public enum TaskStatus
    {
        ASSIGNED,
        ACCEPTED,
        IN_PROGRESS,
        COMPLETED
    }

    public enum AttendanceStatus
    {
        PRESENT,
        ABSENT,
        HALF_DAY
    }

    public enum TimesheetStatus
    {
        PENDING,
        APPROVED,
        REJECTED
    }

    public enum ComplaintStatus
    {
        OPEN,
        IN_PROGRESS,
        RESOLVED
    }
}
