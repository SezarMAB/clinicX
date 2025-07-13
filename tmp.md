Below are the two patterns Spring-Boot teams use most often for **read-only PostgreSQL views** such as `v_dental_chart`. Pick the one that best matches your situation.

---

## 1 ️⃣  Map the view to an **immutable JPA entity**

### a.  Create a composite key for the row

`v_dental_chart` has no primary key, but JPA still needs one.
The natural choice is the pair **`patient_id` + `tooth_number`**.

```java
// key
@AllArgsConstructor @NoArgsConstructor @Getter @Setter @EqualsAndHashCode
public class DentalChartId implements Serializable {
    private UUID patientId;
    private Integer toothNumber;
}
```

### b.  Entity class (read-only)

```java
@Entity
@Table(name = "v_dental_chart")          // maps directly to the view
@Immutable                                // Hibernate-specific but handy
@IdClass(DentalChartId.class)
@Getter @Setter @NoArgsConstructor
public class DentalChartView {

    @Id @Column(name = "patient_id")
    private UUID patientId;

    @Id @Column(name = "tooth_number")
    private Integer toothNumber;

    @Column(name = "condition_code")  private String conditionCode;
    @Column(name = "condition_name")  private String conditionName;
    @Column(name = "color_hex")       private String colorHex;
    private String notes;

    @Column(name = "last_treatment_date")
    private LocalDate lastTreatmentDate;
}
```

* `@Immutable` (Hibernate) or `@ReadOnlyProperty` (Spring) prevents any `INSERT/UPDATE/DELETE`.
* Make sure **`spring.jpa.hibernate.ddl-auto=none`**, otherwise Hibernate might try to drop/recreate the view.

### c.  Repository

```java
@Repository
public interface DentalChartViewRepository
        extends JpaRepository<DentalChartView, DentalChartId> {

    List<DentalChartView> findByPatientId(UUID patientId);
}
```

---

## 2 ️⃣  Skip the entity – use an **interface-based projection**

If you don’t need paging or dynamic filters, a projection keeps the domain model clean and avoids the fake PK:

```java
public interface DentalChartProjection {
    UUID      getPatientId();
    Integer   getToothNumber();
    String    getConditionCode();
    String    getConditionName();
    String    getColorHex();
    String    getNotes();
    LocalDate getLastTreatmentDate();
}

@Repository
public interface DentalChartRepository extends JpaRepository<Patient,UUID> {

    @Query(value = """
        SELECT *
        FROM v_dental_chart
        WHERE patient_id = :patientId
        ORDER BY tooth_number
        """, nativeQuery = true)
    List<DentalChartProjection> findChart(@Param("patientId") UUID patientId);
}
```

* Spring Data maps each row to the projection interface by column name.
* The method returns a **detached, read-only DTO**, so no accidental writes are possible.

---

## Which approach should I choose? 🤔

| Need / Constraint                                        | Recommendation                           |
| -------------------------------------------------------- | ---------------------------------------- |
| Want to reuse JPA paging, sorting, Specifications        | **Entity + @Immutable**                  |
| Very simple read-only query, don’t want “fake” IDs       | **Projection**                           |
| You’ll later migrate the view to a materialized table    | **Entity** (future-proof)                |
| Strict domain-cleanliness (no DB artefacts inside model) | **Projection**, then map to your own DTO |

Both patterns coexist happily in the same codebase, so feel free to mix:
use entities for complex views and projections for lightweight look-ups.

---

### ⚠️  Common pitfalls

1. **DDL-auto** – keep it `none` (or at least `validate`) so Hibernate doesn’t attempt to drop the view.
2. **Missing `@Id`** – JPA refuses to start without it; always supply an artificial key if you go the entity route.
3. **Write attempts** – without `@Immutable`, accidental saves will hit “cannot insert into a view” SQL errors.

With either pattern in place, you can now return the dental chart from your service layer as the DTO expected by your UI.
