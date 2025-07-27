-- Add reference_number column to payments table for tracking external payment references
-- This column is useful for matching payments with bank statements, payment gateway transactions, and check numbers

ALTER TABLE payments 
ADD COLUMN IF NOT EXISTS reference_number VARCHAR(100);

-- Add an index on reference_number for faster lookups
CREATE INDEX IF NOT EXISTS idx_payments_reference_number ON payments(reference_number);

-- Note: H2 doesn't support COMMENT ON COLUMN syntax