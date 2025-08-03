-- V11: Handle duplicate staff emails and update unique constraint
-- This migration removes duplicate emails in the staff table and ensures uniqueness per tenant

-- Step 1: Update duplicate emails to make them unique
-- This will append the row ID to duplicate emails to make them unique
WITH duplicate_emails AS (
    SELECT email, COUNT(*) as email_count
    FROM staff
    GROUP BY email
    HAVING COUNT(*) > 1
),
duplicates AS (
    SELECT s.id, s.email, s.tenant_id, s.user_id,
           ROW_NUMBER() OVER (PARTITION BY s.email ORDER BY s.created_at) as rn
    FROM staff s
    INNER JOIN duplicate_emails de ON s.email = de.email
)
UPDATE staff
SET email = CONCAT(email, '_duplicate_', id)
WHERE id IN (
    SELECT id FROM duplicates WHERE rn > 1
);

-- Step 2: Update any remaining placeholder emails from the old implementation
UPDATE staff
SET email = CONCAT(user_id, '@', tenant_id, '.local')
WHERE email LIKE '%@temp.com' OR email LIKE '%@example.com';

-- Step 3: Drop the existing unique constraint
ALTER TABLE staff DROP CONSTRAINT IF EXISTS staff_email_key;

-- Step 4: Create a new unique constraint that includes tenant_id
-- This allows the same email to be used across different tenants
ALTER TABLE staff ADD CONSTRAINT staff_email_tenant_unique UNIQUE (email, tenant_id);

-- Step 5: Add an index on email for better query performance
CREATE INDEX IF NOT EXISTS idx_staff_email ON staff(email);

-- Step 6: Add an index on user_id and tenant_id for better query performance
CREATE INDEX IF NOT EXISTS idx_staff_user_tenant ON staff(user_id, tenant_id);
