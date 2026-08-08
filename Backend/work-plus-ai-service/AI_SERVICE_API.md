# WorkPlus AI Service API

## Employee Task chat

```http
POST http://localhost:8080/api/ai/chat
Content-Type: application/json
Cookie: jwt=<cookie created by Employee login>
```

Request:

```json
{
  "message": "What tasks are currently pending?"
}
```

Successful response:

```json
{
  "answer": "You currently have one Task in progress...",
  "taskCount": 3
}
```

Rules:

- Required role: `EMPLOYEE`
- Employee identity comes from the verified HttpOnly JWT cookie.
- The service calls Transaction Service's `/api/transactions/tasks/my` API.
- Gemini receives only safe Task fields belonging to the logged-in Employee.
- `GEMINI_API_KEY` must be configured as a backend environment variable.
