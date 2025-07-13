
> You are a **senior Spring-Boot engineer**.
> The project already contains fully-implemented **service interfaces & implementations** in `src/main/java`.
> **Analyse those services** and generate a set of **REST controllers** that expose *every public service method* as a clean, versioned HTTP API—complete with Swagger / OpenAPI annotations.

---

### 1️⃣ Controller requirements

| Topic              | Spec                                                                                                                                             |
| ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Package**        | `com.example.<feature>.controller`                                                                                                               |
| **Class name**     | `<Aggregate>Controller` (e.g., `PatientController`)                                                                                              |
| **Base path**      | `/api/v1/<plural-kebab>` (e.g., `/api/v1/patients`)                                                                                              |
| **Annotations**    | `@RestController`, `@RequestMapping`, `@Validated`, Lombok `@RequiredArgsConstructor`, `@Slf4j`                                                  |
| **Swagger**        | Use `@Tag`, `@Operation`, `@ApiResponse`, `@Parameter`, `@RequestBody`, `@Schema` from **springdoc-openapi** (`io.swagger.v3.oas.annotations.*`) |
| **Validation**     | Annotate request DTO params with `@Valid` / `@NotNull` as required                                                                               |
| **Error handling** | Rely on existing `@ControllerAdvice` (if present) or throw project’s custom exceptions                                                           |
| **Return types**   | • Single resources → `ResponseEntity<Dto>`  <br>• Lists → `Page<Dto>` (with `Pageable` param)                                                    |
| **Mapping rules**  | • `GET` for read, `POST` create, `PUT`/`PATCH` update, `DELETE` delete <br>• Path variables are UUIDs                                            |
| **Security**       | Do **not** add Spring Security annotations (out of MVP scope)                                                                                    |
| **Content-type**   | Controllers accept/produce `application/json`                                                                                                    |
| **HATEOAS**        | Omit unless service already returns it                                                                                                           |

---

### 2️⃣ Output format

For **each service interface** found:

1. Markdown heading `#### <ServiceName> → Controller`
2. One `java` fenced block containing the complete controller source.

    * No explanatory prose **inside** the code block.
    * Include all necessary imports—code must compile.

Repeat until every service is covered.

---

### 3️⃣ Swagger conventions quick reference

```java
@Tag(name = "Patients", description = "Operations related to patient management")
@Operation(
    summary = "Create a new patient",
    description = "Generates a publicFacingId, initialises dental chart records, and returns the created patient."
)
@ApiResponse(responseCode = "201", description = "Patient created", content = @Content(schema = @Schema(implementation = PatientDetailsDto.class)))
@ApiResponse(responseCode = "400", description = "Validation error")
```

*Parameter examples*

```java
@Parameter(name = "id", description = "Patient UUID", required = true)
@PathVariable UUID id
```

---

### 4️⃣ Quality checklist

* Every public service method is reachable via at least one endpoint.
* HTTP verbs and status codes follow REST best practices (`201 CREATED` on POST, etc.).
* Pagination parameters (`page`, `size`, `sort`) accepted where list endpoints exist.
* Swagger annotations accurately reflect path, params, request body, and response.
* Controllers delegate *directly* to services—**no business logic**.
* Code compiles with Java 21 + Spring Boot 3.3 + springdoc-openapi-ui.

---

### 5️⃣ Available context at run-time

* Full source tree under `src/main/java` (services + DTOs + entities).

---

**➡️ Generate the controllers (with Swagger docs) for every implemented service.**
