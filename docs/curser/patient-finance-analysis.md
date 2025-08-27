## Patient Finance Domain Analysis and Optimization Proposal

### Scope
Deep analysis of entities, relationships, and finance-related controllers/services under `patient/`, followed by senior-architect recommendations (with finance best practices) to make finance easy to manage in the UI.

### Entity Model and Relationships (Finance-Focused)
- **Patient**
  - Key fields: `publicFacingId`, demographics, `balance`.
  - Relations: `invoices`, `payments`, `treatments`, `appointments`, `documents`, `labRequests`, `notes`, `dentalChart`.

- **Invoice**
  - Fields: `invoiceNumber` (unique), `issueDate`, `dueDate`, `totalAmount`, `status`.
  - Relations: `patient`, `createdBy` (staff), `items` (invoice items), `payments`.

- **InvoiceItem**
  - Fields: `description`, `amount`.
  - Relations: `invoice`, `treatment` (1:1 via unique constraint).

- **Payment**
  - Fields: `paymentDate`, `amount`, `paymentMethod`, `type` (PAYMENT/CREDIT/REFUND), `status`, `referenceNumber`, `description`.
  - Relations: `invoice` (nullable for advance/unallocated), `patient`, `createdBy`.

- **PaymentPlan**
  - Fields: `planName`, `totalAmount`, `installmentCount`, `installmentAmount`, `startDate`, `endDate`, `frequencyDays`, `status`, `notes`.
  - Relations: `patient`, `invoice`, `createdBy`, `installments`.

- **PaymentPlanInstallment**
  - Fields: `installmentNumber`, `dueDate`, `amount`, `paidAmount`, `paidDate`, `status`, `notes`.
  - Relations: `paymentPlan`.

- **Treatment**
  - Fields: `status`, `cost`, `treatmentDate`, optional `toothNumber`, `treatmentNotes`.
  - Relations: `appointment`, `patient`, `procedure`, optional `doctor`, `createdBy`, `materials`.

- **TreatmentMaterial**
  - Fields: `materialName`, `quantity`, `unit`, `costPerUnit`, `totalCost` (auto = qty × unit cost), `supplier`, `batchNumber`, `notes`.
  - Relations: `treatment`.

- **Procedure**
  - Fields: `procedureCode`, `name`, `defaultCost`, `defaultDurationMinutes`, `isActive`.
  - Relations: `specialty`.

- **Views**
  - `PatientFinancialSummaryView` and `UpcomingAppointmentsView` for reporting/summary.

- **Enums (selected)**
  - `InvoiceStatus`: OPEN, DRAFT, PENDING, UNPAID, PARTIALLY_PAID, PAID, OVERDUE, CANCELLED, COMPLETED.
  - `PaymentType`: PAYMENT, CREDIT, REFUND.
  - `PaymentStatus`: PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED, REFUNDED, VOIDED, DISPUTED, PARTIALLY_REFUNDED.
  - `PaymentPlanStatus`, `InstallmentStatus`.

### Finance Controllers and Capabilities
- **InvoiceControllerApi**: create invoice, add payment to invoice, list patient financial records (paged), next invoice number, recalc patient balance.
- **InvoiceManagementControllerApi**: generate from treatments, update/cancel/status ops, unpaid list, aging report, discount, add/remove items, batch invoice, payment history, clone.
- **PaymentControllerApi**: list/filter, create/update/void, statistics, bulk, method breakdown, apply to invoice, unallocated list, reconcile.
- **PaymentPlanControllerApi**: create, get by id/patient/status, update status, cancel, list installments, record installment payment, overdue/due-between, statistics, reports.
- **FinancialSummaryControllerApi**: patient summary, all summaries, outstanding balances.

### Findings and Gaps
- **Balance consistency**: `Patient.balance` can drift from invoices/payments; manual recalculation endpoint exists.
- **Payment allocation granularity**: Single invoice link or unallocated; no multi-invoice split/partial allocation support.
- **Invoice financial breakdown**: Only `totalAmount`; missing `subTotal`, `discount`, `tax`, `writeOff`, `amountPaid`, `amountDue` for clear UI.
- **Materials billing**: Materials tracked, but their contribution to invoice totals is implicit/unclear for users.
- **State machine**: Rich enums but no explicit state transition guardrails and side-effects.
- **Refunds/credits**: Represented via `Payment.type`, but lack of dedicated credit/refund objects or unified ledger reduces auditability.
- **Reconciliation/audit**: No unified ledger for a single source of truth timeline per patient.
- **Tax/currency rounding**: DTOs for tax exist; schema lacks tax fields and explicit rounding policy.

### Architecture Recommendations (Finance Best Practices)

#### 1) Introduce a Unified Ledger
- Add `LedgerEntry` to record all financial movements with strong typing.
  - Types: CHARGE, DISCOUNT, TAX, ADJUSTMENT, WRITE_OFF, PAYMENT_RECEIPT, REFUND, CREDIT_APPLIED.
  - Links: `patientId`, optional `invoiceId`, optional `paymentId`.
  - Benefits: one audit trail, running balance, easy export, UI-friendly timeline.

Example structure:
```sql
CREATE TABLE ledger_entries (
  id UUID PRIMARY KEY,
  patient_id UUID NOT NULL,
  invoice_id UUID NULL,
  payment_id UUID NULL,
  entry_type VARCHAR(40) NOT NULL,
  amount NUMERIC(10,2) NOT NULL,
  occurred_at TIMESTAMP NOT NULL DEFAULT now(),
  description TEXT NULL,
  metadata JSONB NULL
);
CREATE INDEX idx_ledger_patient_date ON ledger_entries(patient_id, occurred_at);
```

#### 2) Support Payment Allocations (Many-to-Many)
- Add `PaymentAllocation` to distribute a single payment across multiple invoices.
  - Fields: `payment_id`, `invoice_id`, `allocated_amount`, `allocated_at`.
  - Deprecate direct `Payment.invoice_id` in favor of allocations; keep for backward compatibility during migration.

```sql
CREATE TABLE payment_allocations (
  payment_id UUID NOT NULL,
  invoice_id UUID NOT NULL,
  allocated_amount NUMERIC(10,2) NOT NULL,
  allocated_at TIMESTAMP NOT NULL DEFAULT now(),
  PRIMARY KEY (payment_id, invoice_id)
);
CREATE INDEX idx_alloc_invoice ON payment_allocations(invoice_id);
```

#### 3) Enrich Invoice Totals
- Extend `invoices` with explicit totals for transparent UI and simpler queries:
  - `sub_total`, `discount_amount`, `tax_amount`, `adjustment_amount`, `write_off_amount`, `amount_paid` (materialized), `amount_due` (materialized).
- Optionally extend `invoice_items` with `item_type`, `tax_rate`, `discount_amount` to separate materials/procedures/adjustments.

```sql
ALTER TABLE invoices
  ADD COLUMN sub_total NUMERIC(10,2),
  ADD COLUMN discount_amount NUMERIC(10,2) DEFAULT 0,
  ADD COLUMN tax_amount NUMERIC(10,2) DEFAULT 0,
  ADD COLUMN adjustment_amount NUMERIC(10,2) DEFAULT 0,
  ADD COLUMN write_off_amount NUMERIC(10,2) DEFAULT 0,
  ADD COLUMN amount_paid NUMERIC(10,2) DEFAULT 0,
  ADD COLUMN amount_due NUMERIC(10,2) DEFAULT 0;
```

#### 4) Clarify Materials Billing
- Choose a policy and enforce in services:
  - Aggregate materials into treatment item totals (simple), or
  - Itemize materials as separate invoice items (transparent cost breakdown).
- Provide a generation toggle and consistent UI display either way.

#### 5) Enforce State Machine and Invariants
- Centralize invoice status transitions with guardrails:
  - DRAFT → OPEN → UNPAID → PARTIALLY_PAID → PAID; OVERDUE derived by `due_date` and `amount_due > 0`.
  - Maintain `amount_paid` via allocations; compute `amount_due`.
  - Emit domain events to update ledger, analytics, reminders.

#### 6) Make Patient Balance Derived
- Prefer computing `Patient` balance from the ledger. If materialized for performance, keep an immutable ledger + periodic materialization with a forced-recalc endpoint for safety.

#### 7) UI-Driven APIs
- Patient Ledger:
  - `GET /api/v1/ledger/patient/{patientId}?from&to&type&invoiceId` returns entries with running balance.
- Payment Allocation:
  - `POST /api/v1/payments/{paymentId}/allocate` with body `[ { invoiceId, amount } ]`.
- Adjustments/Write-offs:
  - `POST /api/v1/invoice-management/{invoiceId}/write-off` with reason and amount.
- Credit Notes:
  - `POST /api/v1/invoices/{invoiceId}/credit-note` creating negative entries or credit allocations.

#### 8) Reporting and Indexing
- Align tax/report DTOs with schema; create DB views for aging, receivables, collections, unreconciled items.
- Add indexes for common filters: `invoices(patient_id, status, due_date)`, `payments(patient_id, payment_date, status)`, `payment_allocations(invoice_id)`, `payment_plan_installments(due_date, status)`.

#### 9) Currency and Rounding Policy
- Centralize rounding (e.g., HALF_UP at scale 2) and optionally add `currency` per monetary entity (default from tenant).

### Concrete UI Flows Enabled
- **Patient Ledger Tab**: unified timeline with running balance and filters.
- **Unallocated Payments**: list, suggest allocations, bulk allocate.
- **Invoice Detail**: clear totals section (subtotal, discounts, tax, adjustments, write-offs, paid, due).
- **Materials Toggle**: show/hide itemized materials; totals remain consistent.
- **Payment Plans**: installment statuses with quick actions (post payment, defer, write-off).
- **Collections Dashboard**: overdue invoices/installments, unallocated payments, aging buckets.

### Development-Phase Implementation Order
1) Implement `LedgerEntry` domain and repository; hook into invoice/payment services to record entries.
2) Add `PaymentAllocation` and refactor payment-to-invoice linking to use allocations.
3) Extend `Invoice` with explicit totals; compute/maintain in service layer; enforce a state machine for statuses.
4) Decide materials billing policy (aggregate vs itemized) and implement invoice generation accordingly.
5) Add UI-driven endpoints: patient ledger, allocations (bulk), write-offs, credit notes.
6) Add indexes and reporting views; align DTOs with schema (tax/rounding policy).

### Risks and Mitigations
- Data migration: provide scripts and reconciliation checks; dual-write during transition.
- UI performance: paginate ledger; use indexed views/materialized summaries.
- Training: ledger improves transparency; add UI explanations per entry type.

### Quick Wins
- Add payment allocations and ledger tables now; start dual-writing.
- Extend invoice totals; compute due/paid; guard status transitions.
- Add patient ledger endpoint; simple `LedgerEntryDto` with running balance for immediate UI value.


