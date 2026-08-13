# Business Service API Guide

This document describes the completed Client, Project, and Employee-Project
Assignment APIs.

## Service addresses

- API Gateway: `http://localhost:8080`
- Auth base URL through Gateway: `http://localhost:8080/api/auth`
- Business base URL through Gateway: `http://localhost:8080/api/business`

Do not call the internal Auth (`8081`) or Business (`8082`) ports from the
frontend or Postman. API Gateway discovers the target service through Eureka.

The user first logs in through Auth Service. Auth Service stores the JWT in an
HttpOnly cookie named `jwt`. Postman or React must send that cookie with later
requests. Business Service validates the cookie and applies the role rules
listed below.

## Role responsibilities

| Role | Business responsibility |
|---|---|
| `ADMIN` | Read Client and Project data for supervision |
| `HR_HEAD` | Create/update Clients and Projects and manage employee assignments |
| `MANAGER` | View Projects they manage and view their own Project teams |
| `EMPLOYEE` | View Projects assigned to them |

Admin is intentionally excluded from Employee-Project assignment APIs because
staffing is an HR responsibility.

## Supporting Auth lookup APIs

These APIs belong to Auth Service because Auth Service owns user accounts.
Both require `HR_HEAD`.

| Method | Endpoint | Purpose | Success |
|---|---|---|---|
| GET | `/api/auth/users/managers` | Get approved, active Managers for Project forms | `200` |
| GET | `/api/auth/users/employees` | Get approved, active Employees for assignment forms | `200` |

Example lookup response:

```json
[
  {
    "userId": 3,
    "username": "manager1",
    "fullName": "Rahul Patil",
    "email": "manager1@example.com"
  }
]
```

Passwords and account-control fields are never returned.

## Client APIs

Base endpoint: `/api/business/clients`

| Method | Endpoint | Purpose | Required role | Success |
|---|---|---|---|---|
| POST | `/clients` | Create a Client | `HR_HEAD` | `201` |
| GET | `/clients` | View all Clients | `ADMIN`, `HR_HEAD` | `200` |
| GET | `/clients/{clientId}` | View one Client | `ADMIN`, `HR_HEAD` | `200` |
| PUT | `/clients/{clientId}` | Update a Client | `HR_HEAD` | `200` |

POST and PUT request:

```json
{
  "clientName": "ABC Client",
  "companyName": "ABC Technologies",
  "email": "contact@abc.com",
  "contact": "9876543210",
  "address": "Pune, Maharashtra"
}
```

Example response:

```json
{
  "clientId": 1,
  "clientName": "ABC Client",
  "companyName": "ABC Technologies",
  "email": "contact@abc.com",
  "contact": "9876543210",
  "address": "Pune, Maharashtra",
  "createdAt": "2026-07-31T10:00:00"
}
```

## Project APIs

Base endpoint: `/api/business/projects`

| Method | Endpoint | Purpose | Required role | Success |
|---|---|---|---|---|
| POST | `/projects` | Create a Project | `HR_HEAD` | `201` |
| GET | `/projects` | View all Projects | `ADMIN`, `HR_HEAD` | `200` |
| GET | `/projects/{projectId}` | View one Project | `ADMIN`, `HR_HEAD` | `200` |
| PUT | `/projects/{projectId}` | Update a Project | `HR_HEAD` | `200` |
| GET | `/projects/my-managed-projects` | View logged-in Manager's Projects | `MANAGER` | `200` |
| GET | `/projects/my-assigned-projects` | View logged-in Employee's Projects | `EMPLOYEE` | `200` |

POST and PUT request:

```json
{
  "projectName": "TimeX Development",
  "description": "Timesheet management application",
  "clientId": 1,
  "managerId": 3,
  "hrHeadId": 2,
  "startDate": "2026-08-01",
  "endDate": "2026-12-31",
  "status": "ACTIVE"
}
```

Allowed status values:

- `ACTIVE`
- `COMPLETED`
- `ON_HOLD`

`status` is optional during creation and defaults to `ACTIVE`. Dates are
optional, but when both are provided, the end date cannot be before the start
date. `clientId`, `managerId`, and `hrHeadId` must identify valid records. The
Manager and HR must be approved and active.

Example response:

```json
{
  "projectId": 10,
  "projectName": "TimeX Development",
  "description": "Timesheet management application",
  "clientId": 1,
  "clientName": "ABC Client",
  "managerId": 3,
  "managerUsername": "manager1",
  "hrHeadId": 2,
  "hrHeadUsername": "hr1",
  "startDate": "2026-08-01",
  "endDate": "2026-12-31",
  "status": "ACTIVE",
  "createdAt": "2026-07-31T10:30:00"
}
```

## Employee-Project Assignment APIs

Base endpoint: `/api/business/employee-projects`

| Method | Endpoint | Purpose | Required role | Success |
|---|---|---|---|---|
| POST | `/employee-projects` | Assign an Employee to a Project | `HR_HEAD` | `201` |
| GET | `/employee-projects` | View every assignment | `HR_HEAD` | `200` |
| GET | `/employee-projects/project/{projectId}` | View a Project team | `HR_HEAD`, owning `MANAGER` | `200` |
| GET | `/employee-projects/employee/{employeeId}` | View one Employee's assignments | `HR_HEAD` | `200` |
| GET | `/employee-projects/my-projects` | View logged-in Employee's assignments | `EMPLOYEE` | `200` |
| DELETE | `/employee-projects/{employeeProjectId}` | Remove an assignment | `HR_HEAD` | `204` |

Assignment request:

```json
{
  "employeeId": 4,
  "projectId": 10
}
```

The Employee must have role `EMPLOYEE` and must be approved and active. The
same Employee cannot be assigned to the same Project twice.

Example response:

```json
{
  "employeeProjectId": 7,
  "employeeId": 4,
  "employeeUsername": "employee1",
  "employeeFullName": "Priya Sharma",
  "projectId": 10,
  "projectName": "TimeX Development",
  "managerId": 3,
  "managerUsername": "manager1",
  "assignedDate": "2026-07-31T11:00:00"
}
```

## Common HTTP responses

| Status | Meaning |
|---|---|
| `200 OK` | Read or update succeeded |
| `201 Created` | New record created |
| `204 No Content` | Delete succeeded |
| `400 Bad Request` | Invalid JSON, validation failure, or broken business rule |
| `401 Unauthorized` | JWT cookie is missing, invalid, or expired |
| `403 Forbidden` | User is logged in but the role is not allowed |
| `404 Not Found` | Requested ID does not exist |
| `409 Conflict` | Duplicate assignment or record cannot be deleted due to relationships |
| `500 Internal Server Error` | Unexpected server failure |

Example validation error:

```json
{
  "timestamp": "2026-07-31T11:15:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Request validation failed",
  "validationErrors": {
    "managerId": "Manager ID is required"
  }
}
```

## Configuration

Local defaults still work without environment variables. Deployment can
override these settings:

| Environment variable | Used for |
|---|---|
| `DB_URL` | MySQL JDBC URL |
| `DB_USERNAME` | MySQL username |
| `DB_PASSWORD` | MySQL password |
| `JWT_SECRET` | Shared JWT signing secret for Auth and Business services |
| `JWT_EXPIRATION_MS` | Auth token lifetime |
| `AUTH_SERVICE_PORT` | Auth Service port |
| `BUSINESS_SERVICE_PORT` | Business Service port |

Auth and Business services must receive exactly the same `JWT_SECRET`.
