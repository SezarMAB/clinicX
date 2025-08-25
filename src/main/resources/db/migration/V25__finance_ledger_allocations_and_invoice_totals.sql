-- V25: Finance ledger, payment allocations, and invoice totals enrichment

-- Ensure UUID generation is available
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 1) Ledger entries table
CREATE TABLE IF NOT EXISTS ledger_entries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL,
    invoice_id UUID NULL,
    payment_id UUID NULL,
    entry_type VARCHAR(50) NOT NULL,
    amount NUMERIC(10,2) NOT NULL,
    occurred_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    description TEXT NULL,
    CONSTRAINT fk_ledger_patient FOREIGN KEY (patient_id) REFERENCES patients (id) ON DELETE CASCADE,
    CONSTRAINT fk_ledger_invoice FOREIGN KEY (invoice_id) REFERENCES invoices (id) ON DELETE SET NULL,
    CONSTRAINT fk_ledger_payment FOREIGN KEY (payment_id) REFERENCES payments (id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_ledger_patient_date ON ledger_entries(patient_id, occurred_at);

-- 2) Payment allocations table
CREATE TABLE IF NOT EXISTS payment_allocations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_id UUID NOT NULL,
    invoice_id UUID NOT NULL,
    allocated_amount NUMERIC(10,2) NOT NULL,
    allocated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT fk_alloc_payment FOREIGN KEY (payment_id) REFERENCES payments (id) ON DELETE CASCADE,
    CONSTRAINT fk_alloc_invoice FOREIGN KEY (invoice_id) REFERENCES invoices (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_alloc_invoice ON payment_allocations(invoice_id);
CREATE INDEX IF NOT EXISTS idx_alloc_payment ON payment_allocations(payment_id);

-- 3) Extend invoices with totals columns
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS sub_total NUMERIC(10,2);
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS discount_amount NUMERIC(10,2) DEFAULT 0;
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS tax_amount NUMERIC(10,2) DEFAULT 0;
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS adjustment_amount NUMERIC(10,2) DEFAULT 0;
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS write_off_amount NUMERIC(10,2) DEFAULT 0;
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS amount_paid NUMERIC(10,2) DEFAULT 0;
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS amount_due NUMERIC(10,2) DEFAULT 0;

-- 4) Extend invoice_items with item type
ALTER TABLE invoice_items ADD COLUMN IF NOT EXISTS item_type VARCHAR(30);


