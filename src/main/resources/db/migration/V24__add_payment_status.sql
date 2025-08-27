-- Add status column to payments table
ALTER TABLE payments 
ADD COLUMN IF NOT EXISTS status VARCHAR(50) NOT NULL DEFAULT 'COMPLETED';

-- Create index for better query performance
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);