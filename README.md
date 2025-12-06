# User-Loan-Prjct (demo)

This is a small Spring Boot demo application implementing a simple user/loan/backoffice domain.

Summary of implemented features
- Spring Boot application (OpenAPI-generated controllers + handwritten services)
- In-memory H2 database (configured in `src/main/resources/application.yaml`)
- Service layer with interfaces and `*ServiceImpl` implementations
- MapStruct mappers for DTO/entity mapping
- Spring Data JPA repositories with a sample join query (native insert join)
- External API call from a service (`ExternalApiServiceImpl`) using `RestTemplate`
- AspectJ-based service-layer logging + HTTP request/response logging filter
- Comprehensive unit tests for controllers and services (run with Maven)

How to run

Prerequisites:
- JDK 17 installed (project target). Developer machines may have newer JDKs; ensure CI/dev runs with JDK 17 for strict compatibility.
- Maven (the project includes the Maven wrapper `mvnw` / `mvnw.cmd`)

Run tests (recommended):

PowerShell (Windows):
```
$env:JAVA_HOME = 'C:\Path\To\jdk-17'
.\mvnw.cmd test
```

Alternatively (if JDK 17 is not available locally) you can run with a newer JDK but ensure CI enforces JDK 17.

Run application locally:
```
$env:JAVA_HOME = 'C:\Path\To\jdk-17'
.\mvnw.cmd spring-boot:run
```

API & features
- Controllers implement OpenAPI-generated interfaces (see `src/main/resources/openapi.yml`).
- Endpoints provide CRUD for users, loan application and retrieval (with pagination), back-office operations, and an endpoint to call an external API and return the response.

Logging & observability
- Service-layer logging is implemented via an Aspect (`com.example.demo.config.LoggingAspect`).
- HTTP-level request/response logging is implemented via `RequestResponseLoggingFilter` (logs bodies using `ContentCaching*Wrapper`).
	- Warning: the filter logs request/response bodies — avoid enabling in production for sensitive payloads.

Error handling
- Domain exceptions: `ResourceNotFoundException`, `BadRequestException`, `ExternalServiceException`.
- Global exception handler: `com.example.demo.config.GlobalExceptionHandler` maps exceptions to JSON error payloads.

Testing
- Unit tests live under `src/test/java` and cover controllers and service implementations.
- Run `mvnw test` to run the full suite.

**********************************************************
For sample curl request please check CURL_EXAMPLES.md file
**********************************************************

Postman Collection link:
https://hasibul-hassan-5006654.postman.co/workspace/Hasibul's-Workspace~e3845e39-a1b3-449b-89c9-84995214d58d/collection/50619288-9e895663-d6f5-4b35-9f4b-1a11fd2d9c24?action=share&creator=50619288