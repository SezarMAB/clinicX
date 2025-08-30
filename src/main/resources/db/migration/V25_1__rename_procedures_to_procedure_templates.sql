-- Rename existing procedures table to procedure_templates to avoid conflict with new procedures table
ALTER TABLE IF EXISTS procedures RENAME TO procedure_templates;

-- Update any foreign key constraints that reference the old table name
ALTER TABLE IF EXISTS visits_old 
    DROP CONSTRAINT IF EXISTS visits_procedure_id_fkey CASCADE;

ALTER TABLE IF EXISTS visits_old
    ADD CONSTRAINT visits_procedure_id_fkey 
    FOREIGN KEY (procedure_id) 
    REFERENCES procedure_templates(id);