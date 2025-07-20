### 0️⃣ Mission

> You are the **lead test engineer**.
> Analyse the whole source tree (entities, mappers, repos, services, controllers) and produce a **comprehensive automated-test suite**.
> Use the dependencies declared in the Gradle file below (§7).

---

### 1️⃣ Test strategy to implement

| Layer                | Test type    | Spring slice                                                     | Tools / Notes                                                                                                                             |
| -------------------- | ------------ | ---------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| **Domain / Mapper**  | *Unit*       | none                                                             | MapStructMapperTest, value object invariants                                                                                              |
| **Service**          | *Unit*       | `@ExtendWith(MockitoExtension.class)` — mock repos & mappers     | Cover happy path + business-rule violations<br>Verify: publicFacingId generation, invoice sequence, balance recompute, patient-teeth init |
| **Repository**       | *Slice*      | `@DataJpaTest` + **Testcontainers** PostgreSQL                   | Use schema migrated by Flyway; CRUD, custom @Query, Specifications                                                                        |
| **Controller**       | *Slice*      | `@WebMvcTest` + MockMvc                                          | JSON ↔ DTO, validation 400s, pagination params                                                                                            |
| **Full integration** | *End-to-end* | `@SpringBootTest(webEnvironment = RANDOM_PORT)` + Testcontainers | Smoke tests for main flows (create patient → schedule appointment → add treatment → issue invoice → pay)                                  |

Add optional **contract tests**: OpenAPI spec validation using `springdoc-openapi-starter`.

---

### 2️⃣ General rules

* **JUnit 5** (`@Test`, `@Nested`, `@DisplayName`).
* **AssertJ** fluent assertions (`assertThat`).
* **Mockito** for mocking (`@Mock`, `@InjectMocks`).
* **Testcontainers** PostgreSQL 15.4; start once per class with static `@Container`.
* `@TestMethodOrder(OrderAnnotation.class)` for flows where order matters.
* Keep one test class per production class except trivial getters/setters.
* Place tests under `src/test/java`, mirroring the main package structure.
* Use meaningful test data builders (`TestDataFactory`).
* Aim for **> 80 % line coverage** (Jacoco generated, no need to commit report).

---

### 3️⃣ Deliverables / Output format

For every test class:

1. Markdown heading `#### <ProductionClass>Test`
2. One `java` fenced code block with the full test source.
   *No explanatory prose inside the code block.*
3. Repeat until all relevant classes are covered (skip generated MapStruct impls).

Additionally:

* `#### TestContainersConfig` → a shared `PostgresTestContainer.java` utility.
* (Optional) `#### TestDataFactory` → helper builders for common entities/DTOs.

---

### 4️⃣ Coverage checklist (self-evaluation)

* **Services**:

    * patient creation generates unique `publicFacingId`.
    * invoice creation fetches nextval from `invoice_number_seq`.
    * balance updated after invoice/payment.
    * patient-teeth rows inserted exactly once.
* **Repositories**: custom queries return correct projections; Specs filter as expected.
* **Controllers**: validation errors return 400 with descriptive message JSON.
* **Integration**: full happy-path creates rows in DB and returns 2xx.
* Each test is isolated, deterministic, and leaves DB clean (Spring `@Transactional`).

---

### 5️⃣ Recommended file structure

```text
src/test/java
└── com/example/clinic
    ├── common
    │   ├── PostgresTestContainer.java
    │   └── TestDataFactory.java
    ├── mapper
    │   └── ProcedureMapperTest.java
    ├── service
    │   ├── PatientServiceTest.java
    │   └── FinancialServiceTest.java
    ├── repository
    │   ├── PatientRepositoryIT.java
    │   └── InvoiceRepositoryIT.java
    ├── controller
    │   └── PatientControllerTest.java
    └── integration
        └── EndToEndFlowIT.java
```

---

### 6️⃣ Swagger contract test snippet (optional)

```java
@WebMvcTest
@AutoConfigureMockMvc
class OpenApiContractTest {
    @Autowired MockMvc mvc;

    @Test void openApiJsonIsServed() throws Exception {
        mvc.perform(get("/v3/api-docs"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.openapi").value("3.0.1"));
    }
}
```

---

### 7️⃣ Gradle dependencies (already present)

```gradle
testImplementation 'org.springframework.boot:spring-boot-starter-test'
testImplementation 'org.testcontainers:testcontainers:1.21.3'
testImplementation 'org.testcontainers:postgresql:1.19.0'
testImplementation 'org.testcontainers:junit-jupiter:1.19.0'
```

---

**➡️  Generate the complete JUnit 5 test-suite, obeying all rules above.**
