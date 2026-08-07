# Transaction Service API Guide

Transaction APIs are exposed through API Gateway at
`http://localhost:8080/api/transactions`. Transaction Service internally runs
on port `8083`. It records daily work
inside Projects and employee assignments that already exist in Business
Service. It exposes 21 APIs in six modules.

## Authentication

1. Log in using `POST http://localhost:8080/api/auth/login` through Gateway.
2. Postman stores the HttpOnly `jwt` cookie for `localhost`.
3. Call these APIs through port `8080` so Postman sends the cookie and every
   request follows the same Gateway path.

The API gets the username and role from the signed cookie. The user must also
be `APPROVED` and `ACTIVE`.

## All APIs

| Module | Method | Endpoint | Purpose | Access |
|---|---|---|---|---|
| Tasks | POST | `/api/transactions/tasks` | Create and assign a Task | `MANAGER` |
| Tasks | GET | `/api/transactions/tasks/my` | View personal Tasks | `EMPLOYEE` |
| Tasks | GET | `/api/transactions/tasks/managed` | View managed Tasks | `MANAGER` |
| Tasks | PUT | `/api/transactions/tasks/{taskId}` | Edit an unfinished Task | Owning `MANAGER` |
| Tasks | PUT | `/api/transactions/tasks/{taskId}/accept` | Accept an assigned Task | Assigned `EMPLOYEE` |
| Tasks | PUT | `/api/transactions/tasks/{taskId}/progress` | Increase Task progress | Assigned `EMPLOYEE` |
| Timesheets | POST | `/api/transactions/timesheets` | Submit Task hours | `EMPLOYEE` |
| Timesheets | GET | `/api/transactions/timesheets/my` | View personal history | `EMPLOYEE` |
| Timesheets | GET | `/api/transactions/timesheets/review` | View managed Timesheets | `MANAGER` |
| Approvals | POST | `/api/transactions/timesheet-approvals/timesheet/{timesheetId}` | Approve/reject a Timesheet | Owning `MANAGER` |
| Complaints | GET | `/api/transactions/complaints/available-managers` | List connected Managers | `EMPLOYEE` |
| Complaints | POST | `/api/transactions/complaints` | Raise a Complaint | `EMPLOYEE` |
| Complaints | GET | `/api/transactions/complaints/my` | View personal Complaints | `EMPLOYEE` |
| Complaints | GET | `/api/transactions/complaints/assigned` | View assigned Complaints | `MANAGER` |
| Complaints | PUT | `/api/transactions/complaints/{complaintId}/resolve` | Resolve a Complaint | Assigned `MANAGER` |
| Attendance | POST | `/api/transactions/attendance/check-in` | Record today's check-in | `EMPLOYEE` |
| Attendance | PUT | `/api/transactions/attendance/check-out` | Record today's check-out | `EMPLOYEE` |
| Attendance | GET | `/api/transactions/attendance/my` | View personal Attendance | `EMPLOYEE` |
| Attendance | GET | `/api/transactions/attendance/team?date=2026-08-05` | View managed team Attendance | `MANAGER` |
| Reports | GET | `/api/transactions/reports/employees` | View managed Employee summaries | `MANAGER` |
| Reports | GET | `/api/transactions/reports/employees/{employeeId}` | View one managed Employee | `MANAGER` |

## Task workflow

The Manager must own the active Project. The Employee must be active and
assigned to that Project.

### Create

`POST /api/transactions/tasks`

```json
{
  "projectId": 9,
  "employeeId": 33,
  "taskName": "Create login page",
  "taskDescription": "Implement and test the login form",
  "startDate": "2026-08-05",
  "endDate": "2026-08-10"
}
```

Returns `201 Created`. The Task starts as `ASSIGNED` with progress `0`.

### Manager update

`PUT /api/transactions/tasks/{taskId}`

```json
{
  "employeeId": 33,
  "taskName": "Create secured login page",
  "taskDescription": "Implement validation and authentication errors",
  "startDate": "2026-08-05",
  "endDate": "2026-08-12"
}
```

Only the owning Manager can edit it. A completed Task cannot be edited.

### Employee acceptance and progress

Call `PUT /api/transactions/tasks/{taskId}/accept` without a body. Then call:

`PUT /api/transactions/tasks/{taskId}/progress`

```json
{
  "progressPercent": 50,
  "remarks": "Login UI and validation are complete"
}
```

Progress cannot decrease. Values `1-99` produce `IN_PROGRESS`; `100` produces
`COMPLETED`. Completed Tasks cannot be reopened.

## Timesheet and approval workflow

The Employee must accept the Task first. The work date must be within the Task
dates and cannot be in the future.

`POST /api/transactions/timesheets`

```json
{
  "taskId": 12,
  "workDate": "2026-08-05",
  "hoursWorked": 8.0,
  "workDescription": "Implemented login form validation"
}
```

Returns `201 Created` with status `PENDING`. The same Task cannot be submitted
twice for one date, and all entries for an Employee cannot total over 24 hours
for a date.

The owning Manager calls:

`POST /api/transactions/timesheet-approvals/timesheet/{timesheetId}`

```json
{
  "decision": "APPROVED",
  "comments": "Work verified"
}
```

Use `REJECTED` to reject. `PENDING` is not a valid review decision. Only a
pending Timesheet can be reviewed, and a review creates an approval audit row.

## Complaint workflow

First call `GET /api/transactions/complaints/available-managers`. It returns
active Managers connected through the Employee's Project assignments.

`POST /api/transactions/complaints`

```json
{
  "managerId": 32,
  "subject": "Task requirement clarification",
  "description": "I need clarification about the expected report format."
}
```

The selected Manager views assigned Complaints and resolves one with:

`PUT /api/transactions/complaints/{complaintId}/resolve`

```json
{
  "resolution": "The required format was shared with the Employee."
}
```

## Attendance workflow

Check-in and check-out use the server's current date and time; neither needs a
body. An Employee can check in once per day, and check-out requires check-in.
Less than four hours produces `HALF_DAY`; otherwise it produces `PRESENT`.

Manager team Attendance defaults to today. Supply a date as
`?date=2026-08-05`. Results include only Employees assigned to Projects owned
by that Manager.

## Manager reports

Reports contain total and completed Tasks, average progress, approved hours,
and Timesheet counts by status. A Manager can only view Employees assigned to
Projects owned by that Manager.

## Errors

```json
{
  "timestamp": "2026-08-05T12:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "A readable explanation",
  "validationErrors": {}
}
```

| Status | Meaning |
|---|---|
| `400` | Invalid request or workflow transition |
| `401` | JWT is missing, invalid, or expired |
| `403` | Role/record ownership does not permit access |
| `404` | Requested record does not exist |
| `409` | Database uniqueness/relationship conflict |
| `500` | Unexpected server error; inspect the application log |

## Run and test

Start MySQL and all three backend services. In STS, import
`Backend/transaction-service` as an existing Maven project and run
`TransactionServiceApplication` as a Spring Boot App.

With Maven installed, use:

```powershell
cd Backend/transaction-service
mvn test
mvn spring-boot:run
```
