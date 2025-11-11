-- PostgreSQL Schema for Test Management System

-- Test Tasks Table
CREATE TABLE IF NOT EXISTS test_tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    environment VARCHAR(50) NOT NULL CHECK (environment IN ('DEV', 'STAGING', 'PROD')),
    version VARCHAR(50) NOT NULL,
    test_scope VARCHAR(50),
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'RUNNING', 'COMPLETED', 'CANCELLED')),
    priority INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    CONSTRAINT chk_priority CHECK (priority >= 0 AND priority <= 10)
);

-- Test Cases Table
CREATE TABLE IF NOT EXISTS test_cases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(200) NOT NULL,
    description TEXT,
    steps JSONB NOT NULL,
    expected_results TEXT,
    priority INTEGER DEFAULT 0,
    type VARCHAR(20) NOT NULL CHECK (type IN ('FUNCTIONAL', 'PERFORMANCE', 'SECURITY')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('DRAFT', 'APPROVED', 'DEPRECATED')),
    tags JSONB,
    related_requirement VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL
);

-- Test Environments Table
CREATE TABLE IF NOT EXISTS test_environments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    config JSONB,
    status VARCHAR(20) NOT NULL CHECK (status IN ('AVAILABLE', 'MAINTENANCE', 'UNAVAILABLE')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Test Versions Table
CREATE TABLE IF NOT EXISTS test_versions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    product_version VARCHAR(50) NOT NULL,
    baseline_version VARCHAR(50),
    config JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Resource Pools Table
CREATE TABLE IF NOT EXISTS resource_pools (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    type VARCHAR(20) NOT NULL CHECK (type IN ('VM', 'CONTAINER', 'DEVICE')),
    capacity INTEGER NOT NULL CHECK (capacity > 0),
    used INTEGER DEFAULT 0 CHECK (used >= 0),
    config JSONB,
    status VARCHAR(20) NOT NULL CHECK (status IN ('AVAILABLE', 'MAINTENANCE', 'UNAVAILABLE')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_capacity_used CHECK (used <= capacity)
);

-- Quality Reports Table
CREATE TABLE IF NOT EXISTS quality_reports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL REFERENCES test_tasks(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    summary TEXT,
    test_results JSONB,
    defect_stats JSONB,
    performance_metrics JSONB,
    risk_assessment JSONB,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL CHECK (status IN ('GENERATING', 'COMPLETED', 'ARCHIVED'))
);

-- Task-TestCase Association Table
CREATE TABLE IF NOT EXISTS task_test_cases (
    task_id UUID NOT NULL REFERENCES test_tasks(id) ON DELETE CASCADE,
    test_case_id UUID NOT NULL REFERENCES test_cases(id) ON DELETE CASCADE,
    execution_order INTEGER,
    PRIMARY KEY (task_id, test_case_id)
);

-- Create indexes for performance
CREATE INDEX idx_test_tasks_status ON test_tasks(status);
CREATE INDEX idx_test_tasks_created_at ON test_tasks(created_at);
CREATE INDEX idx_test_cases_type ON test_cases(type);
CREATE INDEX idx_test_cases_status ON test_cases(status);
CREATE INDEX idx_quality_reports_task_id ON quality_reports(task_id);
CREATE INDEX idx_test_environments_status ON test_environments(status);
CREATE INDEX idx_resource_pools_status ON resource_pools(status);

-- Create function to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for auto-updating updated_at
CREATE TRIGGER update_test_tasks_updated_at BEFORE UPDATE ON test_tasks
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_test_cases_updated_at BEFORE UPDATE ON test_cases
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_test_environments_updated_at BEFORE UPDATE ON test_environments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_test_versions_updated_at BEFORE UPDATE ON test_versions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_resource_pools_updated_at BEFORE UPDATE ON resource_pools
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
