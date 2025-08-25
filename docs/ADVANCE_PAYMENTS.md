# Advance Payments Feature Documentation

## Overview
The advance payments feature allows patients to make payments before receiving services, creating a credit balance that can be applied to future invoices.

## Key Components

### 1. Data Model
- **Payment Entity**: Extended with `PaymentType` enum (PAYMENT, CREDIT, REFUND)
- **Database**: Supports nullable `invoice_id` for credits not yet applied to invoices

### 2. DTOs
- `AdvancePaymentCreateRequest`: For creating new advance payments
- `AdvancePaymentDto`: Represents advance payment details
- `ApplyAdvancePaymentRequest`: For applying credits to invoices
- `PatientCreditBalanceDto`: Shows patient's credit balance summary

### 3. Service Layer
- `AdvancePaymentService`: Core business logic for advance payments
  - Create advance payments
  - Apply credits to invoices (manual and automatic)
  - Track patient credit balances
  - Handle partial credit applications

### 4. API Endpoints

#### Create Advance Payment
```
POST /api/v1/advance-payments
{
  "patientId": "uuid",
  "amount": 100.00,
  "paymentDate": "2024-01-01",
  "paymentMethod": "CASH",
  "description": "Advance payment",
  "referenceNumber": "REF123"
}
```

#### Apply Credit to Invoice
```
POST /api/v1/advance-payments/apply
{
  "advancePaymentId": "uuid",
  "invoiceId": "uuid",
  "amountToApply": 50.00
}
```

#### Get Patient Credits
```
GET /api/v1/advance-payments/patient/{patientId}
GET /api/v1/advance-payments/patient/{patientId}/unapplied
GET /api/v1/advance-payments/patient/{patientId}/balance
```

#### Auto-Apply Credits
```
POST /api/v1/advance-payments/invoice/{invoiceId}/auto-apply
```

## Business Rules

1. **Credit Creation**:
   - Credits are created with `PaymentType.CREDIT`
   - No invoice association initially (`invoice_id = NULL`)
   - Amount must be positive

2. **Credit Application**:
   - Credits can only be applied to unpaid/partially paid invoices
   - Patient must match between credit and invoice
   - Partial application creates a new credit for the remainder
   - Applied credits are linked to the invoice

3. **Balance Calculation**:
   - Total Credits = Sum of all CREDIT type payments
   - Available Credits = Sum of credits where invoice_id is NULL
   - Applied Credits = Total Credits - Available Credits

4. **Auto-Application**:
   - Applies available credits oldest first
   - Stops when invoice is fully paid or no credits remain
   - Can be triggered during invoice creation or manually

## Security Considerations
- All operations require authentication
- Staff member creating the credit is tracked
- Audit trail maintained for all financial transactions

## Future Enhancements
1. Credit expiration dates
2. Credit transfer between patients
3. Refund credits to original payment method
4. Credit usage reports
5. Notification when credits are available