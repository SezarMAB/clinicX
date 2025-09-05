## Patient Finance Implementation Summary (Phase 1)

### Database (Flyway)
- Added `V25__finance_ledger_allocations_and_invoice_totals.sql`:
  - `ledger_entries` table (with `pgcrypto` extension for UUIDs)
  - `payment_allocations` table + indexes
  - Extended `invoices` with: `sub_total`, `discount_amount`, `tax_amount`, `adjustment_amount`, `write_off_amount`, `amount_paid`, `amount_due`
  - Extended `invoice_items` with `item_type`

### New Enums
- `patient/model/enums/LedgerEntryType.java` (CHARGE, DISCOUNT, TAX, ADJUSTMENT, WRITE_OFF, PAYMENT_RECEIPT, REFUND, CREDIT_APPLIED)
- `patient/model/enums/InvoiceItemType.java` (PROCEDURE, MATERIAL, ADJUSTMENT, DISCOUNT, OTHER)

### New Entities and Repositories
- `patient/model/LedgerEntry.java`
  - Links to `Patient`, optional `Invoice`, optional `Payment`; amount, type, occurredAt, description
- `patient/model/PaymentAllocation.java`
  - Links `Payment` to `Invoice` with `allocatedAmount`, `allocatedAt`
- Repositories
  - `patient/repository/LedgerEntryRepository.java`
  - `patient/repository/PaymentAllocationRepository.java` (added `sumAllocatedAmountByInvoiceId`)

### Services
- `patient/service/LedgerService.java` + `impl/LedgerServiceImpl.java`
  - Records ledger entries; exposes patient ledger query

### Controllers and APIs
- Ledger (new)
  - `patient/controller/api/LedgerControllerApi.java`
  - `patient/controller/impl/LedgerControllerImpl.java`
  - Endpoint: `GET /api/v1/ledger/patient/{patientId}` (paged chronological ledger entries)

- Payments (updated)
  - `patient/controller/api/PaymentControllerApi.java`: added `POST /{paymentId}/allocate` for bulk allocations
  - `patient/controller/impl/PaymentControllerImpl.java`: delegates to service

- Invoice Management (updated)
  - New DTOs: `WriteOffRequest`, `CreditNoteRequest`
  - `patient/controller/api/InvoiceManagementControllerApi.java`: added
    - `POST /invoice-management/{invoiceId}/write-off`
    - `POST /invoice-management/{invoiceId}/credit-note`
  - `patient/controller/impl/InvoiceManagementControllerImpl.java`: implements both

- Invoices (minor)
  - `patient/controller/api/InvoiceControllerApi.java`: added optional `POST /{invoiceId}/recompute-totals` (dev helper)
  - `patient/controller/impl/InvoiceControllerImpl.java`: stubbed `recomputeInvoiceTotals` (returns OK)

### Service Integrations and Behavior Changes
- `patient/service/impl/InvoiceServiceImpl.java`
  - On invoice creation: records ledger `CHARGE`
  - On add payment: records ledger `PAYMENT_RECEIPT`, updates materialized `amountPaid` and `amountDue`
  - Implemented `applyWriteOff`: raises `writeOffAmount`, recomputes `amountDue`, records `WRITE_OFF`
  - Implemented `createCreditNote`: raises `discountAmount`, recomputes `amountDue`, records `DISCOUNT` (negative charge)

- `patient/service/impl/PaymentServiceImpl.java`
  - On payment create: records ledger `PAYMENT_RECEIPT`
  - On `applyPaymentToInvoice`: creates a full `PaymentAllocation`, records `CREDIT_APPLIED`, updates invoice status
  - New `allocatePayment(paymentId, allocations[])`: splits a payment across multiple invoices, records `CREDIT_APPLIED` per allocation, updates invoice status

### DTOs
- `patient/dto/LedgerEntryDto.java`
- `patient/dto/PaymentAllocationItem.java`
- `patient/dto/WriteOffRequest.java`
- `patient/dto/CreditNoteRequest.java`

### Notes and Next Steps (Optional Phase 2)
- Add running balance computation in ledger endpoint responses
- Enforce invoice status transitions via a state machine (derive PAID/UNPAID/PARTIALLY_PAID from paid/due)
- Decide materials billing policy (aggregate vs itemized) in invoice generation
- Add tax computation fields and rounding policy centralization
- Replace recompute stub with service-backed totals recalculation utility


