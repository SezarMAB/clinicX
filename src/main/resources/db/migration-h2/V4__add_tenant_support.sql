/* ===================================================================
   MULTI-TENANT SUPPORT  –  H2 IN-MEMORY VERSION
   Version: 4.0 - Add tenant management for SaaS architecture
   =================================================================== */

-- Enable PostgreSQL-like syntax where possible
SET MODE PostgreSQL;

-----------------------------------------------------------------------
-- TENANT MANAGEMENT TABLE
-----------------------------------------------------------------------

-- Tenants table for multi-tenant support
CREATE TABLE tenants (
    id                      UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    tenant_id               VARCHAR(100) UNIQUE NOT NULL,
    name                    VARCHAR(100) NOT NULL,
    subdomain               VARCHAR(50) UNIQUE NOT NULL,
    realm_name              VARCHAR(100) UNIQUE NOT NULL,
    is_active               BOOLEAN NOT NULL DEFAULT TRUE,
    contact_email           VARCHAR(255),
    contact_phone           VARCHAR(50),
    address                 TEXT,
    subscription_start_date TIMESTAMP,
    subscription_end_date   TIMESTAMP,
    subscription_plan       VARCHAR(50),
    max_users               INTEGER DEFAULT 10,
    max_patients            INTEGER DEFAULT 1000,
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    updated_by              VARCHAR(255)
);

-- Create indexes for better performance
CREATE INDEX idx_tenants_tenant_id ON tenants(tenant_id);
CREATE INDEX idx_tenants_subdomain ON tenants(subdomain);
CREATE INDEX idx_tenants_realm_name ON tenants(realm_name);
CREATE INDEX idx_tenants_is_active ON tenants(is_active);
CREATE INDEX idx_tenants_subscription_end_date ON tenants(subscription_end_date);

-- Add tenant_id column to all existing tables for multi-tenant data isolation
-- This is optional and depends on your multi-tenancy strategy
-- If using separate schemas or databases per tenant, you can skip this

-- Example of adding tenant_id to patients table (uncomment if needed)
-- ALTER TABLE patients ADD COLUMN IF NOT EXISTS tenant_id VARCHAR(100);
-- CREATE INDEX IF NOT EXISTS idx_patients_tenant_id ON patients(tenant_id);

-- Example of adding tenant_id to staff table (uncomment if needed)
-- ALTER TABLE staff ADD COLUMN IF NOT EXISTS tenant_id VARCHAR(100);
-- CREATE INDEX IF NOT EXISTS idx_staff_tenant_id ON staff(tenant_id);

-----------------------------------------------------------------------
-- DEFAULT DATA
-----------------------------------------------------------------------

-- Insert a default tenant for single-tenant mode or development
MERGE INTO tenants (
    tenant_id,
    name,
    subdomain,
    realm_name,
    is_active,
    contact_email,
    subscription_plan,
    subscription_start_date,
    subscription_end_date,
    created_by,
    updated_by
) KEY (tenant_id) VALUES (
    'default',
    'Default Clinic',
    'default',
    'clinicx-dev',
    TRUE,
    'admin@clinicx.com',
    'development',
    CURRENT_TIMESTAMP,
    DATEADD('YEAR', 100, CURRENT_TIMESTAMP),
    'system',
    'system'
);

-----------------------------------------------------------------------
-- TRIGGERS (H2 version)
-----------------------------------------------------------------------

-- Note: H2 doesn't support PostgreSQL-style triggers exactly,
-- so we'll use H2's built-in functionality
-- The updated_at column will be handled by JPA @LastModifiedDate
