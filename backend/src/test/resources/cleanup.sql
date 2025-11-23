-- 清理脚本 - 在每个测试方法执行后清空数据
-- 用于所有Mapper层测试
-- 使用 DELETE FROM 代替 TRUNCATE 以避免外键约束问题

-- 按正确顺序删除数据（先子表，后父表）
DELETE FROM task_test_cases;
DELETE FROM monitoring_data;
DELETE FROM quality_reports;
DELETE FROM test_cases;
DELETE FROM test_tasks;
DELETE FROM test_environments;
DELETE FROM test_versions;
DELETE FROM resource_pools;
DELETE FROM ai_models;
