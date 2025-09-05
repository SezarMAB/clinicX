
Here’s a backend-first plan to model “one Treatment (visit) with many Procedures” while you’re still pre‑prod. I’ll assume Java + Spring Boot + JPA, but the ideas map
to any stack.

Domain Model

- Treatment/Visit: Header for one appointment/encounter; owns procedures.
- Procedure: Billable line item (code/name, tooth/surface, fee, status, timings).
- Lab Case: Optional record linked to a crown procedure for sent/received tracking.
- Naming: If you can, rename “Treatment” to “Encounter/Visit” now to avoid confusion with “Procedure”.

Database Schema

- treatments:
    - id (UUID PK), patient_id (FK), appointment_id (FK), provider_id (FK), date, time, notes, created_at, updated_at, tenant_id
- procedures:
    - id (UUID PK), treatment_id (FK), code, name, tooth_number, surfaces (JSON/array), quantity, unit_fee (DECIMAL), duration_minutes, performed_by_id (FK), status
      (ENUM), started_at, completed_at, updated_at, tenant_id
- lab_cases (optional):
    - id, procedure_id (FK), lab_name, sent_date, due_date, received_date, tracking_no, notes, tenant_id
- Indexes:
    - procedures(treatment_id), procedures(status), procedures(tooth_number), treatments(patient_id, date), all tables (tenant_id)
- Constraints:
    - NOT NULL where appropriate, FK ON DELETE CASCADE for procedures (or soft delete), ENUM for status.

Status Model

- Procedure.status: PLANNED → IN_PROGRESS → SENT_TO_LAB → RECEIVED → COMPLETED (or CANCELLED)
- Treatment.status (derived): optional aggregate (e.g., COMPLETED only if all procedures completed)
- Validation: enforce allowed transitions in service layer; reject illegal jumps.

Money & Units

- Fees: Use DECIMAL/BigDecimal in Java. If you anticipate multi-currency, store currency code or use minor units (cents) + currency.
- Totals: Derive Treatment.totalCost = SUM(procedure.unit_fee × quantity) at read time; persist a cached total only if you need performance.

Spring/JPA Entities

- Treatment:
    - @Entity Treatment { @Id UUID id; @OneToMany(mappedBy="treatment", orphanRemoval=true, cascade=PERSIST) List procedures; … }
    - Prefer no cascade REMOVE unless you intend deletes; consider soft-deletes.
- Procedure:
    - @ManyToOne(fetch = LAZY) Treatment treatment; fields above; @Enumerated(EnumType.STRING) for status
- LabCase:
    - @OneToOne or @ManyToOne to Procedure (one procedure can have one lab case; you may extend later)

DTOs

- TreatmentDto: id, patientId, appointmentId, date, time, providerId, notes, procedureCount, totalCost
- TreatmentDetailsDto: TreatmentDto + procedures: ProcedureDto[]
- ProcedureDto: id, treatmentId, code, name, toothNumber, surfaces[], quantity, unitFee, durationMinutes, performedById, status, startedAt, completedAt, labCase?
- Use MapStruct for mapping; keep DTOs stable and versioned.

Controllers (Spring Boot)

- List treatments: GET /api/v1/treatments/patient/{patientId} with Pageable (size, page, sort= date,desc). Return headers with aggregates.
- Get details: GET /api/v1/treatments/{id} returns TreatmentDetailsDto (use @EntityGraph to fetch procedures).
- Create treatment: POST /api/v1/treatments (create visit header).
- Add procedure: POST /api/v1/treatments/{id}/procedures (create line item).
- Update procedure: PATCH /api/v1/procedures/{id} (status/fee/notes).
- Delete procedure: DELETE /api/v1/procedures/{id} (if allowed).
- Search: POST /api/v1/treatments/search for advanced filters (status, provider, code, date range).

Service Layer

- Transactional boundaries: annotate service methods with @Transactional; validate status transitions; recompute and persist cached totals if you keep them.
- Optimistic locking: add @Version to entities or check updatedAt to prevent lost updates.
- Multi‑tenant: include tenant_id in all tables; enforce in queries (filter or PostgreSQL RLS if you use it).

Validation

- Bean Validation: @NotNull, @DecimalMin("0.00"), @Size on codes/names, custom validator for surfaces/tooth rules.
- Business rules: disallow editing procedures included in submitted claims; allow adjustments via separate correction procedures.

Migrations (pre‑prod fast path)

- Add tables: create procedures and lab_cases.
- Backfill (if you have dev data): for each existing treatment, insert one procedure with name/cost/status from treatment.
- Deprecate fields: stop writing treatmentName/cost/status on Treatment; later remove them.

Queries & Performance

- Aggregates: either compute on read (JOIN + SUM) or maintain cached totals on Treatment updated by triggers/service.
- Pagination/sorting: stick to Spring’s Pageable. You already pass sort as field,dir, keep that.
- Filtering: by procedure status/code/provider (JOIN); index columns used in WHERE.

Lab Workflow

- LabCase: link to the crown procedure; set procedure.status to SENT_TO_LAB/RECEIVED on LabCase changes.
- Background jobs: optional reminders for due/overdue lab cases.

Security

- AuthZ: check roles (DOCTOR, ADMIN) for create/update; lock after claim submission.
- Audit: createdBy/updatedBy on procedures; keep updatedAt to drive UI refresh.

Example Controller Signatures (Spring)

- List: Page<TreatmentDto> list(@PathVariable UUID patientId, Pageable pageable, TreatmentFilter filter)
- Details: TreatmentDetailsDto get(@PathVariable UUID id)
- Create Treatment: TreatmentDto create(@RequestBody CreateTreatmentRequest req)
- Add Procedure: ProcedureDto add(@PathVariable UUID treatmentId, @RequestBody CreateProcedureRequest req)
- Patch Procedure: ProcedureDto patch(@PathVariable UUID id, @RequestBody ProcedurePatch patch)

What to implement first (1–2 sprints)

- Add Procedure entity/table + endpoints.
- Update “create treatment” flow: create header, then first procedure from existing fields.
- Treatment details endpoint + simple procedures table UI.
- Crown statuses (SENT_TO_LAB/RECEIVED/COMPLETED) on Procedure; optional LabCase.

If you share your backend tech (Spring Boot version, DB), I can draft concrete entity classes, repositories, and controller stubs tailored to your stack.
