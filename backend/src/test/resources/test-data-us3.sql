-- 测试数据初始化脚本 - US3 监控和可视化测试
-- 用于：MonitoringControllerIntegrationTest

-- 清空已有数据
TRUNCATE TABLE monitoring_data;
TRUNCATE TABLE test_cases;
TRUNCATE TABLE test_tasks;

-- 插入测试任务
INSERT INTO test_tasks (id, task_name, test_scope, environment, version, created_by, status, priority, created_at, updated_at, deleted_at)
VALUES
(1, '订单模块集成测试', 'CORE', 'TEST', 'v1.5.0', 'zhangsan', 'RUNNING', 'HIGH', NOW(), NOW(), NULL),
(2, '支付流程全量测试', 'FULL', 'STAGING', 'v1.5.0', 'lisi', 'COMPLETED', 'CRITICAL', NOW(), NOW(), NULL),
(3, '用户管理冒烟测试', 'SMOKE', 'DEV', 'v1.5.1', 'wangwu', 'PENDING', 'MEDIUM', NOW(), NOW(), NULL);

-- 插入测试用例（包含执行结果）
INSERT INTO test_cases (id, case_name, module, priority, type, steps, expected_result, ai_generated, ai_confidence, created_by, status, created_at, updated_at, deleted_at)
VALUES
(1, '订单创建-正常流程', '订单管理', 'HIGH', 'FUNCTIONAL',
 '[{"step":"1","action":"选择商品"},{"step":"2","action":"提交订单"}]',
 '{"status":"success"}',
 true, 0.92, 'ai-system', 'PASSED', NOW(), NOW(), NULL),

(2, '订单支付-微信支付', '支付模块', 'CRITICAL', 'INTEGRATION',
 '[{"step":"1","action":"选择微信支付"},{"step":"2","action":"完成支付"}]',
 '{"status":"success","payment_status":"SUCCESS"}',
 true, 0.95, 'ai-system', 'PASSED', NOW(), NOW(), NULL),

(3, '订单取消-异常场景', '订单管理', 'MEDIUM', 'FUNCTIONAL',
 '[{"step":"1","action":"取消订单"}]',
 '{"status":"cancelled"}',
 true, 0.88, 'ai-system', 'FAILED', NOW(), NOW(), NULL),

(4, '退款流程-全额退款', '支付模块', 'HIGH', 'FUNCTIONAL',
 '[{"step":"1","action":"申请退款"},{"step":"2","action":"审核通过"}]',
 '{"status":"refunded"}',
 false, NULL, 'manual', 'PASSED', NOW(), NOW(), NULL);

-- 插入监控数据（时间序列）
-- 任务1的监控数据
INSERT INTO monitoring_data (id, task_id, metric_type, metric_value, unit, recorded_at, created_at)
VALUES
-- 执行时间数据点
(1, 1, 'EXECUTION_TIME', 125.5, 'seconds', DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW()),
(2, 1, 'EXECUTION_TIME', 132.8, 'seconds', DATE_SUB(NOW(), INTERVAL 50 MINUTE), NOW()),
(3, 1, 'EXECUTION_TIME', 118.3, 'seconds', DATE_SUB(NOW(), INTERVAL 40 MINUTE), NOW()),
(4, 1, 'EXECUTION_TIME', 145.2, 'seconds', DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW()),

-- 通过率数据点
(5, 1, 'PASS_RATE', 85.5, 'percent', DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW()),
(6, 1, 'PASS_RATE', 87.2, 'percent', DATE_SUB(NOW(), INTERVAL 50 MINUTE), NOW()),
(7, 1, 'PASS_RATE', 89.0, 'percent', DATE_SUB(NOW(), INTERVAL 40 MINUTE), NOW()),
(8, 1, 'PASS_RATE', 88.5, 'percent', DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW()),

-- CPU使用率
(9, 1, 'CPU_USAGE', 45.2, 'percent', DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW()),
(10, 1, 'CPU_USAGE', 52.3, 'percent', DATE_SUB(NOW(), INTERVAL 50 MINUTE), NOW()),
(11, 1, 'CPU_USAGE', 48.7, 'percent', DATE_SUB(NOW(), INTERVAL 40 MINUTE), NOW()),

-- 内存使用率
(12, 1, 'MEMORY_USAGE', 68.5, 'percent', DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW()),
(13, 1, 'MEMORY_USAGE', 72.1, 'percent', DATE_SUB(NOW(), INTERVAL 50 MINUTE), NOW()),
(14, 1, 'MEMORY_USAGE', 70.8, 'percent', DATE_SUB(NOW(), INTERVAL 40 MINUTE), NOW()),

-- 任务2的监控数据（已完成）
(15, 2, 'EXECUTION_TIME', 1580.2, 'seconds', DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW()),
(16, 2, 'PASS_RATE', 92.3, 'percent', DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW()),
(17, 2, 'CPU_USAGE', 38.5, 'percent', DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW()),
(18, 2, 'MEMORY_USAGE', 65.2, 'percent', DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW()),

-- 任务3的监控数据（待执行）
(19, 3, 'EXECUTION_TIME', 0.0, 'seconds', NOW(), NOW()),
(20, 3, 'PASS_RATE', 0.0, 'percent', NOW(), NOW());

-- 历史趋势数据（最近7天的通过率）
INSERT INTO monitoring_data (task_id, metric_type, metric_value, unit, recorded_at, created_at)
VALUES
(1, 'PASS_RATE', 82.5, 'percent', DATE_SUB(NOW(), INTERVAL 7 DAY), NOW()),
(1, 'PASS_RATE', 84.3, 'percent', DATE_SUB(NOW(), INTERVAL 6 DAY), NOW()),
(1, 'PASS_RATE', 86.7, 'percent', DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),
(1, 'PASS_RATE', 85.9, 'percent', DATE_SUB(NOW(), INTERVAL 4 DAY), NOW()),
(1, 'PASS_RATE', 88.2, 'percent', DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),
(1, 'PASS_RATE', 87.5, 'percent', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PASS_RATE', 89.1, 'percent', DATE_SUB(NOW(), INTERVAL 1 DAY), NOW());

-- 设置自增ID
ALTER TABLE test_tasks AUTO_INCREMENT = 100;
ALTER TABLE test_cases AUTO_INCREMENT = 100;
ALTER TABLE monitoring_data AUTO_INCREMENT = 100;
