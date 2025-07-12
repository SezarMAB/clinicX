

### 0️⃣ Mission

> You are a **senior Spring-Boot engineer**.
> **Analyse** the entire project source tree (entities, DTOs, repositories, projections, specs) **and** the screen requirements expressed in `ui-mockup.html`.
> Produce every **service interface + implementation** required for the MVP so that the UI can work end-to-end.

---

### 1️⃣ Scope & Responsibilities

For each aggregate that **already exists** in the project **and** is referenced by the UI:

| Layer                    | What to generate                                                                                                  | Key points                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| ------------------------ | ----------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Service interface**    | `FooService`                                                                                                      | *Pure contract* (`create…`, `update…`, `find…`, etc.)                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| **Service impl**         | `FooServiceImpl`                                                                                                  | • Annotate `@Service`, implement interface<br>• Wrap write methods in `@Transactional` (class-level or method-level)<br>• Inject repositories & mappers via constructor                                                                                                                                                                                                                                                                                                                                                            |
| **Business rules**       | Implement once, centrally                                                                                         | 1. **Generate `publicFacingId`** on patient creation (human-readable, unique)<br>2. **Allocate sequential `invoiceNumber`** via DB sequence `invoice_number_seq` (use `@Transactional` + `@Query(value="SELECT nextval('invoice_number_seq')", nativeQuery=true)` inside a helper)<br>3. **Re-calculate patient balance** on every invoice/payment mutation (reuse repository methods; mimic trigger logic)<br>4. **Auto-initialise 32 `PatientTeeth` rows** immediately after patient creation (mimic `initialize_patient_teeth`) |
| **DTO ↔ Entity mapping** | MapStruct preferred (`@Mapper(componentModel = "spring")`), fallback to manual                                    | Create new mappers only if not already present.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| **Validation**           | Use `jakarta.validation` on *request* DTOs, enforce in service before save.                                       |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| **Error handling**       | Throw custom exceptions (`NotFoundException`, `BusinessRuleException`) located in `com.example.shared.exception`. |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |

---

### 2️⃣ Implementation conventions

* **Packages**

    * `com.example.<feature>.service` – interfaces
    * `com.example.<feature>.service.impl` – implementations
    * `com.example.<feature>.mapper` – MapStruct mappers (if new)
* **Logging** – `@Slf4j` on implementations.
* **Constructor injection** – no `@Autowired` on fields.
* **Read-only methods** – annotate with `@Transactional(readOnly=true)`.
* **Return types** – DTOs (never entities) or `Page<Dto>` for lists.

---

### 3️⃣ Deliverables / Output format

For **each** service:

1. `#### <Aggregate> Service` (markdown heading)
2. `java` blocks in this order:

    1. Service interface
    2. Implementation class
    3. (If new) Mapper interface
    4. (If new) Exception class

*No explanatory prose inside code blocks.*

---

### 4️⃣ Quality checklist (self-evaluation)

* All UI use-cases can be fulfilled by calling generated services.
* Business-rule code is covered by unit-test hints (`TODO write tests`).
* No duplicate logic; helper methods are `private`.
* Sequence fetch & balance recalculation run inside the same transaction that persists the invoice/payment.
* Patient-teeth initialisation runs only once per patient.

---

### 5️⃣ Input files available at run-time

* `ui-mockup.html` – full HTML of the MVP screen(s).
* **Project sources** – everything under `src/main/java`.

---

**➡️ Generate the complete set of Spring-Boot service interfaces, implementations, mappers, and any supporting classes exactly as specified. Skip features tied to entities not present in the current MVP.**
