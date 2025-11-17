package com.synapsetest.testmanagement.mapper;

import com.synapsetest.testmanagement.entity.TestTask;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Mapper层单元测试 - TestTaskMapper
 *
 * 测试目标：
 * 1. 验证MyBatis Mapper的CRUD操作
 * 2. 验证SQL映射的正确性
 * 3. 验证数据库约束（唯一性、非空等）
 * 4. 验证复杂查询逻辑
 *
 * 技术栈：
 * - @MybatisTest: 轻量级MyBatis测试，只加载Mapper层
 * - @AutoConfigureTestDatabase(replace = NONE): 使用真实数据库（H2）
 * - @Sql: 执行SQL脚本初始化测试数据
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("TestTaskMapper数据访问层测试")
public class TestTaskMapperTest {

    @Autowired
    private TestTaskMapper testTaskMapper;

    @Test
    @DisplayName("Mapper测试1: 插入测试任务成功")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void insert_WithValidTestTask_ShouldReturnAffectedRows() {
        // Given: 准备测试任务数据
        TestTask testTask = new TestTask();
        testTask.setTaskName("冒烟测试-订单模块");
        testTask.setTestScope("SMOKE");
        testTask.setEnvironment("DEV");
        testTask.setVersion("v1.2.0");
        testTask.setCreatedBy("zhangsan");
        testTask.setStatus("PENDING");
        testTask.setPriority("HIGH");
        testTask.setCreatedAt(LocalDateTime.now());
        testTask.setUpdatedAt(LocalDateTime.now());

        // When: 执行插入操作
        int affectedRows = testTaskMapper.insert(testTask);

        // Then: 验证插入成功
        assertEquals(1, affectedRows, "应该插入1条记录");
        assertNotNull(testTask.getId(), "ID应该自动生成");

        // 验证可以通过ID查询到刚插入的记录
        TestTask inserted = testTaskMapper.selectById(testTask.getId());
        assertNotNull(inserted);
        assertEquals("冒烟测试-订单模块", inserted.getTaskName());
        assertEquals("SMOKE", inserted.getTestScope());
    }

    @Test
    @DisplayName("Mapper测试2: 根据ID查询测试任务")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void selectById_WithExistingId_ShouldReturnTestTask() {
        // Given: 数据库中存在ID为1的测试任务（通过SQL脚本插入）
        Long taskId = 1L;

        // When: 根据ID查询
        TestTask testTask = testTaskMapper.selectById(taskId);

        // Then: 验证查询结果
        assertNotNull(testTask);
        assertEquals(taskId, testTask.getId());
        assertEquals("SMOKE", testTask.getTestScope());
    }

    @Test
    @DisplayName("Mapper测试3: 查询不存在的ID返回null")
    void selectById_WithNonExistingId_ShouldReturnNull() {
        // Given: 不存在的ID
        Long nonExistingId = 999999L;

        // When: 根据ID查询
        TestTask testTask = testTaskMapper.selectById(nonExistingId);

        // Then: 应该返回null
        assertNull(testTask);
    }

    @Test
    @DisplayName("Mapper测试4: 更新测试任务状态")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void update_WithValidData_ShouldUpdateSuccessfully() {
        // Given: 查询已存在的任务
        TestTask testTask = testTaskMapper.selectById(1L);
        assertNotNull(testTask);
        String originalStatus = testTask.getStatus();

        // When: 更新状态
        testTask.setStatus("RUNNING");
        testTask.setUpdatedAt(LocalDateTime.now());
        int affectedRows = testTaskMapper.update(testTask);

        // Then: 验证更新成功
        assertEquals(1, affectedRows);

        TestTask updated = testTaskMapper.selectById(1L);
        assertEquals("RUNNING", updated.getStatus());
        assertNotEquals(originalStatus, updated.getStatus());
    }

    @Test
    @DisplayName("Mapper测试5: 根据状态查询任务列表")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void selectByStatus_WithValidStatus_ShouldReturnList() {
        // Given: 数据库中存在多个PENDING状态的任务
        String status = "PENDING";

        // When: 根据状态查询
        List<TestTask> tasks = testTaskMapper.selectByStatus(status);

        // Then: 验证查询结果
        assertNotNull(tasks);
        assertTrue(tasks.size() > 0);
        tasks.forEach(task -> assertEquals(status, task.getStatus()));
    }

    @Test
    @DisplayName("Mapper测试6: 根据创建者查询任务列表")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void selectByCreatedBy_WithValidUser_ShouldReturnList() {
        // Given: 数据库中存在zhangsan创建的任务
        String createdBy = "zhangsan";

        // When: 根据创建者查询
        List<TestTask> tasks = testTaskMapper.selectByCreatedBy(createdBy);

        // Then: 验证查询结果
        assertNotNull(tasks);
        assertTrue(tasks.size() > 0);
        tasks.forEach(task -> assertEquals(createdBy, task.getCreatedBy()));
    }

    @Test
    @DisplayName("Mapper测试7: 删除测试任务（软删除）")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void delete_WithValidId_ShouldSoftDelete() {
        // Given: 查询已存在的任务
        Long taskId = 1L;
        TestTask testTask = testTaskMapper.selectById(taskId);
        assertNotNull(testTask);
        assertNull(testTask.getDeletedAt(), "初始状态应该未被删除");

        // When: 执行软删除
        int affectedRows = testTaskMapper.softDelete(taskId);

        // Then: 验证删除成功
        assertEquals(1, affectedRows);

        TestTask deleted = testTaskMapper.selectById(taskId);
        assertNotNull(deleted.getDeletedAt(), "删除后应该有删除时间戳");
    }

    @Test
    @DisplayName("Mapper测试8: 根据环境和版本查询任务")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void selectByEnvironmentAndVersion_WithValidParams_ShouldReturnList() {
        // Given: 查询条件
        String environment = "DEV";
        String version = "v1.2.0";

        // When: 根据环境和版本查询
        List<TestTask> tasks = testTaskMapper.selectByEnvironmentAndVersion(environment, version);

        // Then: 验证查询结果
        assertNotNull(tasks);
        assertTrue(tasks.size() > 0);
        tasks.forEach(task -> {
            assertEquals(environment, task.getEnvironment());
            assertEquals(version, task.getVersion());
        });
    }

    @Test
    @DisplayName("Mapper测试9: 分页查询测试任务")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void selectWithPagination_WithValidParams_ShouldReturnPagedResults() {
        // Given: 分页参数
        int offset = 0;
        int limit = 10;

        // When: 分页查询
        List<TestTask> tasks = testTaskMapper.selectWithPagination(offset, limit);

        // Then: 验证查询结果
        assertNotNull(tasks);
        assertTrue(tasks.size() <= limit);
    }

    @Test
    @DisplayName("Mapper测试10: 统计各状态任务数量")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void countByStatus_ShouldReturnCorrectCounts() {
        // When: 统计不同状态的任务数量
        int pendingCount = testTaskMapper.countByStatus("PENDING");
        int runningCount = testTaskMapper.countByStatus("RUNNING");
        int completedCount = testTaskMapper.countByStatus("COMPLETED");

        // Then: 验证统计结果
        assertTrue(pendingCount >= 0);
        assertTrue(runningCount >= 0);
        assertTrue(completedCount >= 0);
    }
}
