-- 清理脚本 - 在每个测试方法执行后清空数据
-- 用于所有Mapper层测试和Controller集成测试

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 删除测试表（按照外键依赖顺序）
DROP TABLE IF EXISTS quality_report_test_result;
DROP TABLE IF EXISTS quality_report_risk_assessment;
DROP TABLE IF EXISTS quality_report;
DROP TABLE IF EXISTS monitoring_data;
DROP TABLE IF EXISTS test_cases;

-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;
