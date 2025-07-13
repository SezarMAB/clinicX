**LLM CODE-GENERATION PROMPT
(Produce Java DTO records straight from an HTML mock-up)**

---

### 0 ️⃣ Objective

> **Task for the LLM:** You will receive a *single HTML file* that represents a screen (or set of screens) of a web UI.
> Your job is to **derive and output all Java DTO records**—request, response, and list/projection objects—needed to move the exact data shown in the UI between a Spring-Boot backend and a frontend.
> No entities, no controllers, just the DTO layer.

---

### 1 ️⃣ Input

* **File:** `ui-mockup.html` (pasted in §5 at run time).
* The HTML uses semantic tags, `data-*` attributes, and meaningful class names; treat those as hints for field names.

---

### 2 ️⃣ Output requirements

1. **DTO Java records only** – each inside its own `java` fenced block, nothing else inside the code block.
2. Organise records in a logical hierarchy:

   * `…CreateRequest`, `…UpdateRequest` – for forms or editable panels.
   * `…SummaryDto` – for table/list rows.
   * `…DetailsDto` / `…Response` – for full-screen or tabbed detail views.
   * Nest DTOs as appropriate (e.g., `PatientDetailsDto` contains `List<TreatmentLogDto>`).
3. **Field naming rules**

   * Camel-case, English, no abbreviations unless standard (`id`, `dob`).
   * Infer types:

     | UI pattern                 | Java type    |
          | -------------------------- | ------------ |
     | Date (YYYY-MM-DD)          | `LocalDate`  |
     | ISO date-time or timestamp | `Instant`    |
     | Currency or decimal        | `BigDecimal` |
     | Checkbox / toggle          | `Boolean`    |
     | Key, foreign key           | `UUID`       |
   * If a column clearly shows status (badge, colour, icon), represent it with an **enum** (`Status`, `InvoiceState`, etc.) and emit the enum in addition to the DTOs.
4. **Validation** – add `jakarta.validation` annotations (`@NotNull`, `@Size`, `@Email`, `@Positive`) on *request* DTOs where obvious.
5. **Javadoc** – a one-line comment atop every record explaining where it is used in the UI (“Used in Finance tab invoice list”).
6. **Package hint** – add a comment above each code block suggesting its target package (e.g., `// package: com.example.clinic.dto.patient`).
7. **No dependencies** other than JDK + Jakarta Validation.

---

### 3 ️⃣ Quality checklist (self-evaluation)

* Every distinct piece of visible data in the HTML has a field.
* No redundant or backend-specific fields (e.g., `createdAt`) that are not shown or editable.
* Enums are used wherever the UI clearly limits the possible values.
* Nested DTOs reflect the containment seen in the UI (cards, tabs, expandable rows).
* No business logic, no Lombok, no Spring annotations.

---

### 4 ️⃣ Example snippet (do **not** copy verbatim)

```java
// package: com.example.clinic.dto.finance

/**
 * Line item in the invoice table of the Finance tab.
 */
public record InvoiceRowDto(
        UUID id,
        String invoiceNumber,
        LocalDate issueDate,
        BigDecimal totalAmount,
        InvoiceStatus status) { }

public enum InvoiceStatus { UNPAID, PARTIALLY_PAID, PAID }
```

---

### 5 ️⃣ HTML mock-up (inserted at run-time)

```html
<!-- The full mockup will appear here -->
```

---

**➡️ Generate the complete set of Java DTO records (and any enums) required to support the entire UI.**
