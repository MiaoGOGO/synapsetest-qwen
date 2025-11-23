-- MySQL Schema for Test Management System

-- Create database
CREATE DATABASE IF NOT EXISTS test_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE test_management;

-- Test Tasks Table
CREATE TABLE IF NOT EXISTS test_tasks (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    environment VARCHAR(50) NOT NULL CHECK (environment IN ('DEV', 'TEST', 'STAGING', 'PROD')),
    version VARCHAR(50) NOT NULL,
    test_scope VARCHAR(50),
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'RUNNING', 'COMPLETED', 'CANCELLED')),
    priority INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    CONSTRAINT chk_priority CHECK (priority >= 0 AND priority <= 10)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Test Cases Table
CREATE TABLE IF NOT EXISTS test_cases (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    title VARCHAR(200) NOT NULL,
    description TEXT,
    steps JSON NOT NULL,
    expected_result TEXT,
    priority INTEGER DEFAULT 0,
    type VARCHAR(20) NOT NULL CHECK (type IN ('FUNCTIONAL', 'PERFORMANCE', 'SECURITY')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('DRAFT', 'APPROVED', 'DEPRECATED')),
    tags JSON,
    related_requirement VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Test Environments Table
CREATE TABLE IF NOT EXISTS test_environments (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    url VARCHAR(500),
    config JSON,
    status VARCHAR(20) NOT NULL CHECK (status IN ('AVAILABLE', 'MAINTENANCE', 'UNAVAILABLE')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Test Versions Table
CREATE TABLE IF NOT EXISTS test_versions (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    product_version VARCHAR(50) NOT NULL,
    release_date DATE,
    config JSON,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Resource Pools Table
CREATE TABLE IF NOT EXISTS resource_pools (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    type VARCHAR(20) NOT NULL CHECK (type IN ('VM', 'CONTAINER', 'DEVICE')),
    capacity INTEGER NOT NULL CHECK (capacity > 0),
    allocated INTEGER DEFAULT 0 CHECK (allocated >= 0),
    location VARCHAR(200),
    config JSON,
    status VARCHAR(20) NOT NULL CHECK (status IN ('AVAILABLE', 'MAINTENANCE', 'UNAVAILABLE')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_capacity_allocated CHECK (allocated <= capacity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- AI Models Table
CREATE TABLE IF NOT EXISTS ai_models (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(100) NOT NULL,
    version VARCHAR(50) NOT NULL,
    description TEXT,
    file_path VARCHAR(500),
    security_status VARCHAR(20) NOT NULL CHECK (security_status IN ('PENDING', 'IN_REVIEW', 'APPROVED', 'REJECTED')),
    vulnerability_scan_result TEXT,
    last_scan_time TIMESTAMP,
    compliance_status VARCHAR(20) NOT NULL CHECK (compliance_status IN ('COMPLIANT', 'NON_COMPLIANT', 'PENDING')),
    metrics JSON,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Monitoring Data Table
CREATE TABLE IF NOT EXISTS monitoring_data (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    task_id CHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'RUNNING', 'COMPLETED', 'FAILED', 'CANCELLED')),
    progress INTEGER CHECK (progress >= 0 AND progress <= 100),
    executed_cases INTEGER CHECK (executed_cases >= 0),
    total_cases INTEGER CHECK (total_cases >= 0),
    passed_cases INTEGER CHECK (passed_cases >= 0),
    failed_cases INTEGER CHECK (failed_cases >= 0),
    skipped_cases INTEGER CHECK (skipped_cases >= 0),
    start_time TIMESTAMP,
    estimated_end_time TIMESTAMP,
    actual_end_time TIMESTAMP,
    resource_usage JSON,
    performance_metrics JSON,
    timestamp TIMESTAMP,
    environment VARCHAR(50),
    version VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES test_tasks(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Quality Reports Table
CREATE TABLE IF NOT EXISTS quality_reports (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    task_id CHAR(36) NOT NULL,
    name VARCHAR(200) NOT NULL,
    summary TEXT,
    test_results JSON,
    defect_stats JSON,
    performance_metrics JSON,
    risk_assessment JSON,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL CHECK (status IN ('GENERATING', 'COMPLETED', 'ARCHIVED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES test_tasks(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Task-TestCase Association Table
CREATE TABLE IF NOT EXISTS task_test_cases (
    task_id CHAR(36) NOT NULL,
    test_case_id CHAR(36) NOT NULL,
    execution_order INTEGER,
    PRIMARY KEY (task_id, test_case_id),
    FOREIGN KEY (task_id) REFERENCES test_tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (test_case_id) REFERENCES test_cases(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create indexes for performance
CREATE INDEX idx_test_tasks_status ON test_tasks(status);
CREATE INDEX idx_test_tasks_created_at ON test_tasks(created_at);
CREATE INDEX idx_test_cases_type ON test_cases(type);
CREATE INDEX idx_test_cases_status ON test_cases(status);
CREATE INDEX idx_quality_reports_task_id ON quality_reports(task_id);
CREATE INDEX idx_quality_reports_status ON quality_reports(status);
CREATE INDEX idx_quality_reports_generated_at ON quality_reports(generated_at);
CREATE INDEX idx_test_environments_status ON test_environments(status);
CREATE INDEX idx_resource_pools_status ON resource_pools(status);
CREATE INDEX idx_ai_models_security_status ON ai_models(security_status);
CREATE INDEX idx_ai_models_compliance_status ON ai_models(compliance_status);
CREATE INDEX idx_ai_models_name ON ai_models(name);
CREATE INDEX idx_monitoring_data_task_id ON monitoring_data(task_id);
CREATE INDEX idx_monitoring_data_status ON monitoring_data(status);
CREATE INDEX idx_monitoring_data_timestamp ON monitoring_data(timestamp);
