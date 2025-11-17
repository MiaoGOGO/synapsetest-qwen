-- 清理脚本 - 在每个测试方法执行后清空数据
-- 用于所有Mapper层测试

-- 清空测试数据
TRUNCATE TABLE monitoring_data;
TRUNCATE TABLE test_cases;
TRUNCATE TABLE test_tasks;

-- 重置自增ID
ALTER TABLE test_tasks AUTO_INCREMENT = 1;
ALTER TABLE test_cases AUTO_INCREMENT = 1;
ALTER TABLE monitoring_data AUTO_INCREMENT = 1;
