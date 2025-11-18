package com.synapsetest.testmanagement.service;

import com.synapsetest.testmanagement.dto.TestTaskRequest;
import com.synapsetest.testmanagement.dto.response.TestTaskResponse;
import com.synapsetest.testmanagement.exception.ResourceNotFoundException;
import com.synapsetest.testmanagement.exception.ValidationException;
import com.synapsetest.testmanagement.mapper.TestTaskMapper;
import com.synapsetest.testmanagement.model.TestTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * TestTask Service (MyBatis version)
 * Business logic for test task management
 *
 * Task: T030 [US1] Implement TestTaskService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestTaskService {

    private final TestTaskMapper testTaskMapper;
    private final TestRecommendationService recommendationService;

    /**
     * Create a new test task with AI recommendations
     */
    public TestTaskResponse createTestTask(TestTaskRequest request, String username) {
        log.info("Creating test task: {} by user: {}", request.getName(), username);

        // Get AI recommendations
        TestTaskResponse.TestRecommendation recommendation =
                recommendationService.getTestRecommendation(request);

        // Create test task entity
        TestTask testTask = new TestTask();
        testTask.setId(UUID.randomUUID().toString());
        testTask.setName(request.getName());
        testTask.setDescription(request.getDescription());
        testTask.setEnvironment(request.getEnvironment());
        testTask.setVersion(request.getVersion());
        testTask.setTestScope(request.getTestScope());
        testTask.setStatus(TestTask.Status.PENDING.name());
        testTask.setPriority(request.getPriority() != null ? request.getPriority() : 0);
        testTask.setCreatedBy(username);
        testTask.setCreatedAt(LocalDateTime.now());
        testTask.setUpdatedAt(LocalDateTime.now());

        // Save to database
        testTaskMapper.insert(testTask);

        log.info("Test task created successfully with ID: {}", testTask.getId());

        // Convert to response DTO
        return convertToResponse(testTask, recommendation);
    }

    /**
     * Get test task by ID
     */
    public TestTaskResponse getTestTaskById(String id) {
        TestTask testTask = testTaskMapper.selectById(id);
        if (testTask == null) {
            throw new ResourceNotFoundException("TestTask", "id", id);
        }

        return convertToResponse(testTask, null);
    }

    /**
     * Get all test tasks
     */
    public List<TestTaskResponse> getAllTestTasks() {
        return testTaskMapper.selectAll()
                .stream()
                .map(task -> convertToResponse(task, null))
                .collect(Collectors.toList());
    }

    /**
     * Get test tasks by status
     */
    public List<TestTaskResponse> getTestTasksByStatus(String status) {
        return testTaskMapper.selectByStatus(status)
                .stream()
                .map(task -> convertToResponse(task, null))
                .collect(Collectors.toList());
    }

    /**
     * Start a test task
     */
    public TestTaskResponse startTestTask(String id) {
        TestTask testTask = testTaskMapper.selectById(id);
        if (testTask == null) {
            throw new ResourceNotFoundException("TestTask", "id", id);
        }

        if (!TestTask.Status.PENDING.name().equals(testTask.getStatus())) {
            throw new ValidationException("Test task must be in PENDING status to start");
        }

        testTask.setStatus(TestTask.Status.RUNNING.name());
        testTask.setUpdatedAt(LocalDateTime.now());
        testTaskMapper.update(testTask);

        log.info("Test task started: {}", id);

        return convertToResponse(testTask, null);
    }

    /**
     * Cancel a test task
     */
    public TestTaskResponse cancelTestTask(String id) {
        TestTask testTask = testTaskMapper.selectById(id);
        if (testTask == null) {
            throw new ResourceNotFoundException("TestTask", "id", id);
        }

        if (TestTask.Status.COMPLETED.name().equals(testTask.getStatus())) {
            throw new ValidationException("Cannot cancel a completed test task");
        }

        testTask.setStatus(TestTask.Status.CANCELLED.name());
        testTask.setUpdatedAt(LocalDateTime.now());
        testTaskMapper.update(testTask);

        log.info("Test task cancelled: {}", id);

        return convertToResponse(testTask, null);
    }

    /**
     * Convert entity to response DTO
     */
    private TestTaskResponse convertToResponse(TestTask task, TestTaskResponse.TestRecommendation recommendation) {
        TestTaskResponse response = new TestTaskResponse();
        response.setId(task.getId());
        response.setName(task.getName());
        response.setDescription(task.getDescription());
        response.setEnvironment(task.getEnvironment());
        response.setVersion(task.getVersion());
        response.setTestScope(task.getTestScope());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        response.setCreatedBy(task.getCreatedBy());
        response.setRecommendation(recommendation);

        return response;
    }
}
