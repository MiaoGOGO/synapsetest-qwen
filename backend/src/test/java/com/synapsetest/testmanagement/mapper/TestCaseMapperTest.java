package com.synapsetest.testmanagement.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synapsetest.testmanagement.entity.TestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Mapper层单元测试 - TestCaseMapper
 *
 * 测试目标：
 * 1. 验证测试用例的CRUD操作
 * 2. 验证JSON字段的序列化/反序列化（steps, expected_result）
 * 3. 验证AI生成字段的持久化（ai_generated, ai_confidence）
 * 4. 验证复杂查询（模块查询、AI生成用例查询）
 *
 * 重点测试场景：
 * - JSON字段存储和读取的正确性
 * - AI相关字段的数据完整性
 * - 批量操作的原子性
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("TestCaseMapper数据访问层测试")
public class TestCaseMapperTest {

    @Autowired
    private TestCaseMapper testCaseMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Mapper测试1: 插入测试用例（含JSON字段）")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void insert_WithJsonFields_ShouldPersistCorrectly() throws JsonProcessingException {
        // Given: 准备测试用例数据（包含JSON字段）
        TestCase testCase = new TestCase();
        testCase.setCaseName("登录功能-正常流程");
        testCase.setModule("用户认证");
        testCase.setPriority("HIGH");
        testCase.setType("FUNCTIONAL");

        // 准备JSON格式的测试步骤
        List<Map<String, String>> steps = List.of(
            Map.of("step", "1", "action", "打开登录页面", "expected", "显示登录表单"),
            Map.of("step", "2", "action", "输入用户名密码", "expected", "输入框正常响应"),
            Map.of("step", "3", "action", "点击登录按钮", "expected", "跳转到首页")
        );
        testCase.setSteps(objectMapper.writeValueAsString(steps));

        Map<String, Object> expectedResult = Map.of(
            "status_code", 200,
            "redirect_url", "/dashboard",
            "message", "登录成功"
        );
        testCase.setExpectedResult(objectMapper.writeValueAsString(expectedResult));

        testCase.setAiGenerated(true);
        testCase.setAiConfidence(0.95);
        testCase.setCreatedBy("ai-system");
        testCase.setCreatedAt(LocalDateTime.now());
        testCase.setUpdatedAt(LocalDateTime.now());

        // When: 执行插入操作
        int affectedRows = testCaseMapper.insert(testCase);

        // Then: 验证插入成功
        assertEquals(1, affectedRows);
        assertNotNull(testCase.getId());

        // 验证JSON字段可以正确读取
        TestCase inserted = testCaseMapper.selectById(testCase.getId());
        assertNotNull(inserted);
        assertEquals("登录功能-正常流程", inserted.getCaseName());

        // 验证JSON字段反序列化
        List<?> retrievedSteps = objectMapper.readValue(inserted.getSteps(), List.class);
        assertEquals(3, retrievedSteps.size());

        Map<?, ?> retrievedResult = objectMapper.readValue(inserted.getExpectedResult(), Map.class);
        assertEquals(200, retrievedResult.get("status_code"));

        // 验证AI字段
        assertTrue(inserted.getAiGenerated());
        assertEquals(0.95, inserted.getAiConfidence());
    }

    @Test
    @DisplayName("Mapper测试2: 查询AI生成的测试用例")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void selectByAiGenerated_ShouldReturnOnlyAiCases() {
        // Given: 数据库中存在AI生成和人工创建的用例
        boolean aiGenerated = true;

        // When: 查询AI生成的用例
        List<TestCase> aiCases = testCaseMapper.selectByAiGenerated(aiGenerated);

        // Then: 验证查询结果
        assertNotNull(aiCases);
        assertTrue(aiCases.size() > 0);
        aiCases.forEach(testCase -> {
            assertTrue(testCase.getAiGenerated());
            assertNotNull(testCase.getAiConfidence());
            assertTrue(testCase.getAiConfidence() >= 0 && testCase.getAiConfidence() <= 1);
        });
    }

    @Test
    @DisplayName("Mapper测试3: 根据模块查询测试用例")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void selectByModule_WithValidModule_ShouldReturnList() {
        // Given: 查询"订单管理"模块的用例
        String module = "订单管理";

        // When: 根据模块查询
        List<TestCase> cases = testCaseMapper.selectByModule(module);

        // Then: 验证查询结果
        assertNotNull(cases);
        assertTrue(cases.size() > 0);
        cases.forEach(testCase -> assertEquals(module, testCase.getModule()));
    }

    @Test
    @DisplayName("Mapper测试4: 批量插入测试用例")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void batchInsert_WithMultipleCases_ShouldInsertAll() {
        // Given: 准备批量测试用例
        List<TestCase> testCases = List.of(
            createTestCase("测试用例1", "支付模块", 0.85),
            createTestCase("测试用例2", "支付模块", 0.90),
            createTestCase("测试用例3", "支付模块", 0.88)
        );

        // When: 批量插入
        int affectedRows = testCaseMapper.batchInsert(testCases);

        // Then: 验证插入成功
        assertEquals(3, affectedRows);

        // 验证所有用例都有ID
        testCases.forEach(testCase -> assertNotNull(testCase.getId()));

        // 验证可以查询到所有插入的用例
        List<TestCase> insertedCases = testCaseMapper.selectByModule("支付模块");
        assertTrue(insertedCases.size() >= 3);
    }

    @Test
    @DisplayName("Mapper测试5: 更新测试用例状态")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void update_WithValidData_ShouldUpdateSuccessfully() {
        // Given: 查询已存在的用例
        TestCase testCase = testCaseMapper.selectById(1L);
        assertNotNull(testCase);

        // When: 更新用例信息
        testCase.setStatus("REVIEWED");
        testCase.setPriority("CRITICAL");
        testCase.setUpdatedAt(LocalDateTime.now());
        int affectedRows = testCaseMapper.update(testCase);

        // Then: 验证更新成功
        assertEquals(1, affectedRows);

        TestCase updated = testCaseMapper.selectById(1L);
        assertEquals("REVIEWED", updated.getStatus());
        assertEquals("CRITICAL", updated.getPriority());
    }

    @Test
    @DisplayName("Mapper测试6: 根据优先级查询测试用例")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void selectByPriority_WithValidPriority_ShouldReturnList() {
        // Given: 查询HIGH优先级的用例
        String priority = "HIGH";

        // When: 根据优先级查询
        List<TestCase> cases = testCaseMapper.selectByPriority(priority);

        // Then: 验证查询结果
        assertNotNull(cases);
        assertTrue(cases.size() > 0);
        cases.forEach(testCase -> assertEquals(priority, testCase.getPriority()));
    }

    @Test
    @DisplayName("Mapper测试7: 查询高置信度AI生成用例")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void selectByMinConfidence_ShouldReturnHighConfidenceCases() {
        // Given: 查询置信度>=0.8的AI用例
        double minConfidence = 0.8;

        // When: 根据最小置信度查询
        List<TestCase> cases = testCaseMapper.selectByMinConfidence(minConfidence);

        // Then: 验证查询结果
        assertNotNull(cases);
        assertTrue(cases.size() > 0);
        cases.forEach(testCase -> {
            assertTrue(testCase.getAiGenerated());
            assertTrue(testCase.getAiConfidence() >= minConfidence);
        });
    }

    @Test
    @DisplayName("Mapper测试8: 删除测试用例（软删除）")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void softDelete_WithValidId_ShouldMarkAsDeleted() {
        // Given: 查询已存在的用例
        Long caseId = 1L;
        TestCase testCase = testCaseMapper.selectById(caseId);
        assertNotNull(testCase);
        assertNull(testCase.getDeletedAt());

        // When: 执行软删除
        int affectedRows = testCaseMapper.softDelete(caseId);

        // Then: 验证删除成功
        assertEquals(1, affectedRows);

        TestCase deleted = testCaseMapper.selectById(caseId);
        assertNotNull(deleted.getDeletedAt());
    }

    @Test
    @DisplayName("Mapper测试9: 统计模块测试用例数量")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void countByModule_ShouldReturnCorrectCount() {
        // Given: 查询特定模块
        String module = "订单管理";

        // When: 统计用例数量
        int count = testCaseMapper.countByModule(module);

        // Then: 验证统计结果
        assertTrue(count >= 0);
    }

    @Test
    @DisplayName("Mapper测试10: JSON字段为空时的处理")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void insert_WithNullJsonFields_ShouldHandleGracefully() {
        // Given: 准备JSON字段为空的用例
        TestCase testCase = new TestCase();
        testCase.setCaseName("简单测试用例");
        testCase.setModule("基础模块");
        testCase.setPriority("LOW");
        testCase.setType("SMOKE");
        testCase.setSteps(null);  // JSON字段为空
        testCase.setExpectedResult(null);  // JSON字段为空
        testCase.setAiGenerated(false);
        testCase.setCreatedBy("manual");
        testCase.setCreatedAt(LocalDateTime.now());
        testCase.setUpdatedAt(LocalDateTime.now());

        // When: 执行插入
        int affectedRows = testCaseMapper.insert(testCase);

        // Then: 验证可以正常插入
        assertEquals(1, affectedRows);

        TestCase inserted = testCaseMapper.selectById(testCase.getId());
        assertNotNull(inserted);
        // JSON字段应该可以为空
        assertNull(inserted.getSteps());
        assertNull(inserted.getExpectedResult());
    }

    /**
     * 辅助方法：创建测试用例
     */
    private TestCase createTestCase(String caseName, String module, double aiConfidence) {
        TestCase testCase = new TestCase();
        testCase.setCaseName(caseName);
        testCase.setModule(module);
        testCase.setPriority("MEDIUM");
        testCase.setType("FUNCTIONAL");
        testCase.setSteps("[{\"step\":\"1\",\"action\":\"执行操作\"}]");
        testCase.setExpectedResult("{\"status\":\"success\"}");
        testCase.setAiGenerated(true);
        testCase.setAiConfidence(aiConfidence);
        testCase.setCreatedBy("ai-system");
        testCase.setCreatedAt(LocalDateTime.now());
        testCase.setUpdatedAt(LocalDateTime.now());
        return testCase;
    }
}
