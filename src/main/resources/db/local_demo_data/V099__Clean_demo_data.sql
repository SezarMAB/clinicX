-- Clean up existing demo data inserted by V100__Load_demo_data.sql
-- Delete in reverse dependency order using the ID prefixes used in V100

-- Optional safety: wrap in transaction for local/dev use
BEGIN;

-- Dependent clinical/ops data
DELETE FROM procedure_surfaces WHERE procedure_id::text LIKE 'a0000001-%';
DELETE FROM lab_cases WHERE id::text LIKE 'b0000001-%';
DELETE FROM procedure_materials WHERE id::text LIKE 'c0000001-%';
DELETE FROM procedures WHERE id::text LIKE 'a0000001-%';
DELETE FROM visits WHERE id::text LIKE '90000001-%';
DELETE FROM appointments WHERE id::text LIKE '80000001-%';

-- Documents, notes, labs, charts
DELETE FROM documents WHERE id::text LIKE '08000001-%';
DELETE FROM ledger_entries WHERE id::text LIKE '07000001-%';
DELETE FROM lab_requests WHERE id::text LIKE '06000001-%';
DELETE FROM notes WHERE id::text LIKE '05000001-%';
DELETE FROM dental_charts WHERE id::text LIKE '04000001-%';

-- Financials: allocations -> payments/items -> invoices
DELETE FROM payment_allocations WHERE id::text LIKE '01000001-%';
DELETE FROM payment_plan_installments WHERE id::text LIKE '03000001-%';
DELETE FROM payment_plans WHERE id::text LIKE '02000001-%';
DELETE FROM payments WHERE id::text LIKE 'f0000001-%';
DELETE FROM invoice_items WHERE id::text LIKE 'e0000001-%';
DELETE FROM invoices WHERE id::text LIKE 'd0000001-%';

-- Patients last after dependents
DELETE FROM patients WHERE id::text LIKE '70000001-%';

-- Access control and staff links
DELETE FROM user_tenant_access_roles WHERE user_tenant_access_id::text LIKE '50000001-%';
DELETE FROM user_tenant_access WHERE id::text LIKE '50000001-%';
DELETE FROM staff_roles WHERE staff_id::text LIKE '40000001-%';
DELETE FROM staff_specialties WHERE staff_id::text LIKE '40000001-%';
DELETE FROM staff WHERE id::text LIKE '40000001-%';

-- Tenants
DELETE FROM tenants WHERE id::text LIKE '30000001-%' OR tenant_id IN ('demo-dental','demo-general','demo-ortho','demo-cardio');

-- Master data (procedures & specialties)
DELETE FROM procedure_templates WHERE id::text LIKE '60000001-%';
DELETE FROM specialties WHERE id::text LIKE '20000001-%' 
  OR name IN ('General Dentistry','Orthodontics','Endodontics','Periodontics','Oral Surgery','Pediatric Dentistry','Prosthodontics','General Medicine','Cardiology','Dermatology');
DELETE FROM specialty_types WHERE id::text LIKE '10000001-%' 
  OR code IN ('DENTAL','GP','ORTHO','CARDIO','PEDS');

COMMIT;

