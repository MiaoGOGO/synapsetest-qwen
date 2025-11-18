package com.synapsetest.testmanagement.service;

import com.synapsetest.testmanagement.model.TestTask;
import com.synapsetest.testmanagement.dto.TestTaskRequest;
import com.synapsetest.testmanagement.dto.response.TestTaskResponse;
import com.synapsetest.testmanagement.exception.ResourceNotFoundException;
import com.synapsetest.testmanagement.exception.ValidationException;
import com.synapsetest.testmanagement.mapper.TestTaskMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 测试任务服务 - 智能调度场景 (US1)
 * TDD测试用例
 */
@SpringBootTest
@DisplayName("测试任务服务 - 智能调度场景")
class TestTaskServiceTest {

    @MockBean
    private TestTaskMapper testTaskMapper;

    @MockBean
    private TestRecommendationService recommendationService;

    @Autowired
    private TestTaskService testTaskService;

    @Test
    @DisplayName("场景1.1: 创建冒烟测试任务成功")
    void createSmokeTestTask_WithValidInput_ShouldReturnTask() {
        // Given: 有效的任务请求数据
        TestTaskRequest request = new TestTaskRequest();
        request.setName("登录模块冒烟测试");
        request.setDescription("验证登录功能核心流程");
        request.setEnvironment("DEV");
        request.setVersion("v2.1.0");
        request.setTestScope("SMOKE");
        request.setPriority(8);

        when(testTaskMapper.insert(any(TestTask.class))).thenReturn(1);

        // When: 调用创建服务
        TestTaskResponse response = testTaskService.createTestTask(request, "zhangsan");

        // Then: 任务创建成功
        assertNotNull(response.getId());
        assertEquals("登录模块冒烟测试", response.getName());
        assertEquals("PENDING", response.getStatus());
        assertEquals("zhangsan", response.getCreatedBy());
        assertNotNull(response.getCreatedAt());
        verify(testTaskMapper, times(1)).insert(any(TestTask.class));
    }

    @Test
    @DisplayName("场景1.2: 任务名称为空时创建失败")
    void createTask_WithEmptyName_ShouldThrowValidationException() {
        // Given: 空名称
        TestTaskRequest request = new TestTaskRequest();
        request.setName("");
        request.setEnvironment("DEV");
        request.setVersion("v2.1.0");

        // When & Then: 抛出验证异常
        ValidationException exception = assertThrows(ValidationException.class,
            () -> testTaskService.createTestTask(request, "zhangsan"));

        assertTrue(exception.getMessage().contains("名称不能为空"));
    }

    @Test
    @DisplayName("场景1.3: 无效环境类型时创建失败")
    void createTask_WithInvalidEnvironment_ShouldThrowException() {
        // Given: 无效环境
        TestTaskRequest request = new TestTaskRequest();
        request.setName("测试任务");
        request.setEnvironment("INVALID_ENV");
        request.setVersion("v2.1.0");

        // When & Then: 抛出异常
        assertThrows(ValidationException.class,
            () -> testTaskService.createTestTask(request, "zhangsan"));
    }

    @Test
    @DisplayName("场景1.4: 启动PENDING状态任务成功")
    void startTask_FromPendingStatus_ShouldSucceed() {
        // Given: PENDING状态的任务
        TestTask task = createTestTask("task-001", "PENDING");
        when(testTaskMapper.selectById("task-001")).thenReturn(task);
        when(testTaskMapper.update(any())).thenReturn(1);

        // When: 启动任务
        TestTaskResponse response = testTaskService.startTestTask("task-001");

        // Then: 状态变为RUNNING
        assertEquals("RUNNING", response.getStatus());
    }

    @Test
    @DisplayName("场景1.5: 启动已完成任务失败")
    void startTask_FromCompletedStatus_ShouldThrowException() {
        // Given: COMPLETED状态的任务
        TestTask task = createTestTask("task-001", "COMPLETED");
        when(testTaskMapper.selectById("task-001")).thenReturn(task);

        // When & Then: 抛出异常
        assertThrows(IllegalStateException.class,
            () -> testTaskService.startTestTask("task-001"));
    }

    @Test
    @DisplayName("场景1.6: 获取不存在的任务抛出异常")
    void getTaskById_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        // Given: 不存在的ID
        when(testTaskMapper.selectById("non-existing")).thenReturn(null);

        // When & Then
        assertThrows(ResourceNotFoundException.class,
            () -> testTaskService.getTestTaskById("non-existing"));
    }

    // Helper methods
    private TestTask createTestTask(String id, String status) {
        TestTask task = new TestTask();
        task.setId(id);
        task.setName("测试任务");
        task.setEnvironment("DEV");
        task.setVersion("v1.0.0");
        task.setStatus(status);
        task.setPriority(5);
        task.setCreatedBy("tester");
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return task;
    }
}
