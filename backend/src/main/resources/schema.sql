-- MySQL Schema for SynapseTest - AI驱动测试任务管理系统
-- 将MongoDB数据迁移到MySQL

-- 1. 监控数据表 - 存储测试任务的实时监控指标
CREATE TABLE IF NOT EXISTS monitoring_data (
    id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '唯一标识符',
    task_id VARCHAR(36) NOT NULL COMMENT '关联的测试任务ID',
    status VARCHAR(20) NOT NULL COMMENT '状态：PENDING, RUNNING, COMPLETED, FAILED, CANCELLED',
    progress INT DEFAULT 0 COMMENT '进度百分比 (0-100)',
    executed_cases INT DEFAULT 0 COMMENT '已执行的测试用例数',
    total_cases INT DEFAULT 0 COMMENT '总测试用例数',
    passed_cases INT DEFAULT 0 COMMENT '通过的测试用例数',
    failed_cases INT DEFAULT 0 COMMENT '失败的测试用例数',
    skipped_cases INT DEFAULT 0 COMMENT '跳过的测试用例数',
    start_time DATETIME COMMENT '开始时间',
    estimated_end_time DATETIME COMMENT '预计结束时间',
    actual_end_time DATETIME COMMENT '实际结束时间',
    resource_usage JSON COMMENT '资源使用情况，存储CPU、内存等数据',
    performance_metrics JSON COMMENT '性能指标，存储响应时间、吞吐量等数据',
    timestamp DATETIME NOT NULL COMMENT '数据时间戳',
    environment VARCHAR(100) COMMENT '环境信息',
    version VARCHAR(50) COMMENT '版本信息',
    INDEX idx_task_id (task_id),
    INDEX idx_status (status),
    INDEX idx_environment (environment),
    INDEX idx_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. 质量报告表 - 存储测试执行的质量分析报告
CREATE TABLE IF NOT EXISTS quality_report (
    id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '唯一标识符',
    task_id VARCHAR(36) NOT NULL COMMENT '关联的测试任务ID',
    name VARCHAR(255) NOT NULL COMMENT '报告名称',
    summary TEXT COMMENT '报告摘要',
    generated_at DATETIME NOT NULL COMMENT '生成时间',
    status VARCHAR(20) NOT NULL COMMENT '状态：GENERATING, COMPLETED, ARCHIVED',
    defect_stats JSON COMMENT '缺陷统计，按严重程度分类',
    performance_metrics JSON COMMENT '性能指标汇总',
    INDEX idx_task_id (task_id),
    INDEX idx_status (status),
    INDEX idx_generated_at (generated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. 测试结果表 - 存储质量报告中的测试结果详情
CREATE TABLE IF NOT EXISTS quality_report_test_result (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    report_id VARCHAR(36) NOT NULL COMMENT '关联的质量报告ID',
    test_case_id VARCHAR(36) NOT NULL COMMENT '测试用例ID',
    test_case_name VARCHAR(255) NOT NULL COMMENT '测试用例名称',
    status VARCHAR(20) NOT NULL COMMENT '执行状态：PASSED, FAILED, SKIPPED, BLOCKED',
    execution_time BIGINT COMMENT '执行时间（毫秒）',
    error TEXT COMMENT '错误信息',
    screenshot VARCHAR(500) COMMENT '截图路径',
    executed_at DATETIME COMMENT '执行时间',
    INDEX idx_report_id (report_id),
    INDEX idx_test_case_id (test_case_id),
    INDEX idx_status (status),
    FOREIGN KEY (report_id) REFERENCES quality_report(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. 风险评估表 - 存储质量报告中的风险评估信息
CREATE TABLE IF NOT EXISTS quality_report_risk_assessment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    report_id VARCHAR(36) NOT NULL COMMENT '关联的质量报告ID',
    overall_risk VARCHAR(20) NOT NULL COMMENT '总体风险等级：LOW, MEDIUM, HIGH, CRITICAL',
    risk_score DOUBLE DEFAULT 0.0 COMMENT '风险分数 (0.0-1.0)',
    high_risk_modules JSON COMMENT '高风险模块列表',
    recommendations JSON COMMENT '改进建议列表',
    module_risk_scores JSON COMMENT '各模块风险分数映射',
    INDEX idx_report_id (report_id),
    INDEX idx_overall_risk (overall_risk),
    UNIQUE KEY uk_report_id (report_id),
    FOREIGN KEY (report_id) REFERENCES quality_report(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. AI模型表 - 存储AI模型信息
CREATE TABLE IF NOT EXISTS ai_model (
    id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '唯一标识符',
    name VARCHAR(255) NOT NULL COMMENT '模型名称',
    version VARCHAR(50) NOT NULL COMMENT '模型版本',
    description TEXT COMMENT '模型描述',
    file_path VARCHAR(500) COMMENT '模型文件路径',
    security_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '安全状态：PENDING, IN_REVIEW, APPROVED, REJECTED',
    vulnerability_scan_result TEXT COMMENT '漏洞扫描结果',
    last_scan_time DATETIME COMMENT '最后扫描时间',
    compliance_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '合规状态：COMPLIANT, NON_COMPLIANT, PENDING',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    metrics JSON COMMENT '模型性能指标',
    INDEX idx_name_version (name, version),
    INDEX idx_security_status (security_status),
    INDEX idx_compliance_status (compliance_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 为支持UUID生成
DELIMITER //
CREATE FUNCTION generate_uuid_v4() RETURNS CHAR(36)
BEGIN
    SET @uuid := UUID();
    RETURN @uuid;
END//
DELIMITER ;

-- 初始数据插入示例（可选）
INSERT INTO ai_model (id, name, version, description, security_status, compliance_status, created_at, updated_at)
SELECT generate_uuid_v4(), 'Default Test Model', '1.0.0', '默认测试用例生成模型', 'APPROVED', 'COMPLIANT', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM ai_model WHERE name = 'Default Test Model' AND version = '1.0.0');
