**LLM CODE-GENERATION PROMPT
(Build Spring-Data JPA repositories from the existing codebase + UI needs)**

---

### 0 ️⃣ Mission

> You are a senior Spring-Boot engineer.
> **Analyse** the current project (all Java *entities* and *DTOs*) **plus** the screen requirements expressed in `ui-mockup.html`.
> For every aggregate that is both **implemented in the project** *and* **displayed / queried in the mock-up**, generate a *production-ready* Spring-Data JPA repository interface that fully supports the UI’s data-access needs.

---

### 1 ️⃣ Repository requirements

For each eligible entity:

1. **Extend** the correct Spring-Data base:
   `JpaRepository<Entity, IdType>`
   Add `JpaSpecificationExecutor<Entity>` when complex filtering is useful.
2. **Custom query methods**

   * Use JPQL or native `@Query` where derived-query names become unreadable.
   * Optimise with **`LEFT JOIN FETCH`** or `EntityGraph` to prevent N+1 problems shown in the UI.
3. **Projections & slices**

   * Provide **interface-based projections** for list/table views (only the columns the UI renders).
   * Expose methods like `Page<PatientListProjection> findAllProjectedBy(Pageable pageable);`
4. **Specifications**

   * Create a `…Specifications` helper class per aggregate (e.g., `PatientSpecifications.bySearchTerm()`), enabling free-text search & filter chips visible in the mock-up.
5. **Pagination & Sorting**

   * Every list method must accept a `Pageable` argument and return `Page<…>` or `Slice<…>`.
6. **Naming & packaging**

   * Place interfaces in `com.example.<feature>.repository`.
   * Projections in `com.example.<feature>.projection`.
   * Specifications in `com.example.<feature>.spec`.
7. **Skip** entities that are **not part of the current MVP codebase**, even if referenced in the HTML.

---

### 2 ️⃣ Deliverables / Output format

Produce for **each** repository:

* `#### <EntityName>` (markdown heading)
* `java blocks` in this order:

   1. Projection interface(s) (if any)
   2. Specifications class (if any)
   3. Repository interface

No explanatory prose inside code blocks.

---

### 3 ️⃣ Quality checklist (self-evaluation)

* Every query method clearly maps to a concrete UI requirement (search bar, filter, table, detail view).
* No over-fetching: projections include only needed fields.
* Provide at least one example of `@EntityGraph` or `JOIN FETCH` to solve an N+1 listed in the mock-up.
* Specifications are composable (`and`, `or`).
* All list methods honour `Pageable`.
* Repositories compile with Java 21, Spring-Boot 3.3+.

---

### 4 ️⃣ Context files available at runtime

* `ui-mockup.html` – full HTML of the current screen(s).
* All existing Java source under `src/main/java`.

---

**➡️ Generate the complete set of Spring-Data JPA repositories, projections, and specification helpers exactly as described above.**
