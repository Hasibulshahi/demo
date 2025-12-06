# cURL Examples for User-Loan-Prjct APIs

All examples assume the application is running locally at `http://localhost:8080`.

Note: set `JAVA_HOME` to JDK 17 and start the app before running these commands:

```powershell
$env:JAVA_HOME = 'C:\Path\To\jdk-17'
.\mvnw.cmd spring-boot:run
```

---

## Users

1) Get all users

```bash
curl -sS http://localhost:8080/api/v1/users
```

2) Create a user (POST /api/v1/users)

```bash
curl -sS -X POST http://localhost:8080/api/v1/users \
  -H 'Content-Type: application/json' \
  -d '{
    "name":"John Doe",
    "email":"john.doe@example.com",
    "phone":"+60123456789",
    "address":"Jalan 1, 10000, selangor, malaysia"
  }'
```

3) Get user by id (GET /api/v1/users/{id})

```bash
curl -sS http://localhost:8080/api/v1/users/1
```

4) Update user (PUT /api/v1/users/{id})

```bash
curl -sS -X PUT http://localhost:8080/api/v1/users/1 \
  -H 'Content-Type: application/json' \
  -d '{
    "name":"John Updated",
    "email":"john.updated@example.com",
    "phone":"+60111111111",
    "address":"789 New St, City, Country"
  }'
```

5) Delete user (DELETE /api/v1/users/{id})

```bash
curl -sS -X DELETE http://localhost:8080/api/v1/users/1
```

---

## Loans

1) Apply for a loan (POST /api/v1/loans/apply)

```bash
curl -sS -X POST http://localhost:8080/api/v1/loans/apply \
  -H 'Content-Type: application/json' \
  -d '{
    "userId": 1,
    "amount": 5000.0,
    "interestRate": 7.5,
    "endDate": "2026-12-31",
    "loanType": "Home Loan"
  }'
```

2) Get loans by user with pagination (GET /api/v1/loans/user/{userId})

```bash
curl -sS "http://localhost:8080/api/v1/loans/user/1?page=0&size=20"
```

3) Search loans by username and/or loan type with pagination (GET /api/v1/loans/search)

Search by username only:
```bash
curl -sS "http://localhost:8080/api/v1/loans/search?username=John%20Doe&page=0&size=20"
```

Search by loan type only:
```bash
curl -sS "http://localhost:8080/api/v1/loans/search?loanType=Home%20Loan&page=0&size=20"
```

Search by both username and loan type:
```bash
curl -sS "http://localhost:8080/api/v1/loans/search?username=John%20Doe&loanType=Home%20Loan&page=0&size=20"
```

Search with custom pagination (page and size):
```bash
curl -sS "http://localhost:8080/api/v1/loans/search?username=John%20Doe&page=1&size=10"
```

---

## BackOffice

1) Get all backoffice records (GET /api/v1/backoffice)

```bash
curl -sS "http://localhost:8080/api/v1/backoffice?page=0&size=20"
```

2) Create a backoffice record (POST /api/v1/backoffice)

```bash
curl -sS -X POST http://localhost:8080/api/v1/backoffice \
  -H 'Content-Type: application/json' \
  -d '{
    "userId": 1,
    "loanId": 10,
    "appliedDate": "2025-12-06",
    "loanApproval": "PENDING"
  }'
```

3) Get backoffice by user id (GET /api/v1/backoffice/user/{id})

```bash
curl -sS http://localhost:8080/api/v1/backoffice/user/1
```

4) Update backoffice (PUT /api/v1/backoffice/user/{id})

```bash
curl -sS -X PUT http://localhost:8080/api/v1/backoffice/user/1 \
  -H 'Content-Type: application/json' \
  -d '{
    "userId": 1,
    "loanId": 10,
    "appliedDate": "2025-12-06",
    "loanApproval": "APPROVED"
  }'
```

---

## External API call

Call external API endpoint (GET /api/v1/external-call?url=...)

```bash
curl -sS "http://localhost:8080/api/v1/external-call?url=https://www.google.com"
```

---

Notes & tips
- Use double quotes around the full URL when adding query parameters in PowerShell.
- For requests that modify data (POST/PUT), include the `Content-Type: application/json` header.
- The external-call endpoint returns a JSON object with `statusCode`, `responseBody`, `url`, and `timestamp`.
