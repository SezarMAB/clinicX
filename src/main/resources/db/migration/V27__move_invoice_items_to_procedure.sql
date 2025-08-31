-- V27: Link invoice_items to procedures instead of visits (dev-only breaking change)

-- 1) Add new column and FK to procedures
ALTER TABLE invoice_items
    ADD COLUMN IF NOT EXISTS procedure_id UUID;

ALTER TABLE invoice_items
    ADD CONSTRAINT fk_invoice_items_procedure
        FOREIGN KEY (procedure_id) REFERENCES procedures (id) ON DELETE RESTRICT;

-- 2) Backfill procedure_id from visit_id (pick first procedure of the visit deterministically)
UPDATE invoice_items ii
SET procedure_id = sub.pid
FROM (
    SELECT DISTINCT ON (p.visit_id) p.visit_id, p.id AS pid
    FROM procedures p
    ORDER BY p.visit_id, p.created_at ASC NULLS LAST, p.id
) sub
WHERE ii.visit_id IS NOT NULL
  AND ii.procedure_id IS NULL
  AND sub.visit_id = ii.visit_id;

-- 3) Ensure uniqueness: one invoice item per procedure (only when procedure_id is set)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes 
        WHERE schemaname = current_schema() 
          AND indexname = 'ux_invoice_items_procedure_id'
    ) THEN
        CREATE UNIQUE INDEX ux_invoice_items_procedure_id 
            ON invoice_items(procedure_id) 
            WHERE procedure_id IS NOT NULL;
    END IF;
END $$;

-- 4) Drop unique constraint on visit_id if exists, then drop column
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_type = 'UNIQUE'
          AND table_name = 'invoice_items'
          AND constraint_name = 'invoice_items_visit_id_key'
    ) THEN
        ALTER TABLE invoice_items DROP CONSTRAINT invoice_items_visit_id_key;
    END IF;
EXCEPTION WHEN undefined_object THEN
    -- ignore
END $$;

ALTER TABLE invoice_items
    DROP COLUMN IF EXISTS visit_id;
