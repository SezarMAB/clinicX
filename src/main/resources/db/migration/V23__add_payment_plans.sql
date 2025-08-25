-- Create payment_plans table
CREATE TABLE IF NOT EXISTS payment_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL,
    invoice_id UUID NOT NULL,
    plan_name VARCHAR(100) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    installment_count INTEGER NOT NULL CHECK (installment_count > 0),
    installment_amount DECIMAL(10,2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    frequency_days INTEGER,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    notes TEXT,
    created_by UUID,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_payment_plan_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    CONSTRAINT fk_payment_plan_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
    CONSTRAINT fk_payment_plan_created_by FOREIGN KEY (created_by) REFERENCES staff(id) ON DELETE SET NULL
);

-- Create payment_plan_installments table
CREATE TABLE IF NOT EXISTS payment_plan_installments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_plan_id UUID NOT NULL,
    installment_number INTEGER NOT NULL,
    due_date DATE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    paid_amount DECIMAL(10,2) DEFAULT 0,
    paid_date DATE,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    notes TEXT,
    
    CONSTRAINT fk_installment_payment_plan FOREIGN KEY (payment_plan_id) REFERENCES payment_plans(id) ON DELETE CASCADE,
    CONSTRAINT unique_plan_installment_number UNIQUE (payment_plan_id, installment_number)
);

-- Create indexes for better performance
CREATE INDEX idx_payment_plans_patient_id ON payment_plans(patient_id);
CREATE INDEX idx_payment_plans_invoice_id ON payment_plans(invoice_id);
CREATE INDEX idx_payment_plans_status ON payment_plans(status);
CREATE INDEX idx_payment_plans_start_date ON payment_plans(start_date);

CREATE INDEX idx_installments_payment_plan_id ON payment_plan_installments(payment_plan_id);
CREATE INDEX idx_installments_due_date ON payment_plan_installments(due_date);
CREATE INDEX idx_installments_status ON payment_plan_installments(status);

-- Add trigger to update payment_plans.updated_at
CREATE OR REPLACE FUNCTION update_payment_plans_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_payment_plans_updated_at_trigger
    BEFORE UPDATE ON payment_plans
    FOR EACH ROW
    EXECUTE FUNCTION update_payment_plans_updated_at();