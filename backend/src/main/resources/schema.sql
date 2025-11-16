-- MySQL Schema for Test Management System

-- Create database
CREATE DATABASE IF NOT EXISTS test_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE test_management;

-- Test Tasks Table
CREATE TABLE IF NOT EXISTS test_tasks (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    environment VARCHAR(50) NOT NULL CHECK (environment IN ('DEV', 'STAGING', 'PROD')),
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
    expected_results TEXT,
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
    baseline_version VARCHAR(50),
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
    used INTEGER DEFAULT 0 CHECK (used >= 0),
    config JSON,
    status VARCHAR(20) NOT NULL CHECK (status IN ('AVAILABLE', 'MAINTENANCE', 'UNAVAILABLE')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_capacity_used CHECK (used <= capacity)
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
CREATE INDEX idx_test_environments_status ON test_environments(status);
CREATE INDEX idx_resource_pools_status ON resource_pools(status);
