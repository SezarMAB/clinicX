-- Add specialty support for realm-per-type architecture
BEGIN;
-- Add specialty column to tenants table
ALTER TABLE tenants ADD COLUMN specialty VARCHAR(50) NOT NULL DEFAULT 'CLINIC';

-- Create specialty types registry table
CREATE TABLE specialty_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    features VARCHAR(255), -- Array of feature codes
    realm_name VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Insert default specialties
INSERT INTO specialty_types (code, name, features, realm_name) VALUES
('CLINIC', 'General Clinic', 'ALL', 'clinic-realm'),
('DENTAL', 'Dental Clinic', 'DENTAL, APPOINTMENTS', 'dental-realm'),
('APPOINTMENTS', 'Appointments Only', 'APPOINTMENTS', 'appointments-realm');

-- Create user-tenant access table for multi-tenant relationships
CREATE TABLE user_tenant_access (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id VARCHAR(255) NOT NULL, -- Keycloak user ID
    tenant_id VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    UNIQUE(user_id, tenant_id),
    FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id) ON DELETE CASCADE
);

-- Create index for performance
CREATE INDEX idx_user_tenant_access_user_id ON user_tenant_access(user_id);
CREATE INDEX idx_user_tenant_access_tenant_id ON user_tenant_access(tenant_id);
CREATE INDEX idx_tenants_specialty ON tenants(specialty);

COMMIT;
