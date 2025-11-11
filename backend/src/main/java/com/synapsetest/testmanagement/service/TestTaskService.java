package com.synapsetest.testmanagement.service;

import com.synapsetest.testmanagement.dto.TestTaskRequest;
import com.synapsetest.testmanagement.dto.TestTaskResponse;
import com.synapsetest.testmanagement.exception.ResourceNotFoundException;
import com.synapsetest.testmanagement.exception.ValidationException;
import com.synapsetest.testmanagement.model.TestTask;
import com.synapsetest.testmanagement.repository.TestTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * TestTask Service
 * Business logic for test task management
 *
 * Task: T030 [US1] Implement TestTaskService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestTaskService {

    private final TestTaskRepository testTaskRepository;
    private final TestRecommendationService recommendationService;

    /**
     * Create a new test task with AI recommendations
     */
    @Transactional
    public TestTaskResponse createTestTask(TestTaskRequest request, String username) {
        log.info("Creating test task: {} by user: {}", request.getName(), username);

        // Get AI recommendations
        TestTaskResponse.TestRecommendation recommendation =
                recommendationService.getTestRecommendation(request);

        // Create test task entity
        TestTask testTask = new TestTask();
        testTask.setName(request.getName());
        testTask.setDescription(request.getDescription());
        testTask.setEnvironment(request.getEnvironment());
        testTask.setVersion(request.getVersion());
        testTask.setTestScope(request.getTestScope());
        testTask.setStatus(TestTask.Status.PENDING.name());
        testTask.setPriority(request.getPriority() != null ? request.getPriority() : 0);
        testTask.setCreatedBy(username);

        // Save to database
        TestTask saved = testTaskRepository.save(testTask);

        log.info("Test task created successfully with ID: {}", saved.getId());

        // Convert to response DTO
        return convertToResponse(saved, recommendation);
    }

    /**
     * Get test task by ID
     */
    public TestTaskResponse getTestTaskById(UUID id) {
        TestTask testTask = testTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestTask", "id", id));

        return convertToResponse(testTask, null);
    }

    /**
     * Get all test tasks with pagination
     */
    public Page<TestTaskResponse> getAllTestTasks(Pageable pageable) {
        return testTaskRepository.findAll(pageable)
                .map(task -> convertToResponse(task, null));
    }

    /**
     * Get test tasks by status
     */
    public List<TestTaskResponse> getTestTasksByStatus(String status) {
        return testTaskRepository.findByStatus(status)
                .stream()
                .map(task -> convertToResponse(task, null))
                .collect(Collectors.toList());
    }

    /**
     * Start a test task
     */
    @Transactional
    public TestTaskResponse startTestTask(UUID id) {
        TestTask testTask = testTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestTask", "id", id));

        if (!TestTask.Status.PENDING.name().equals(testTask.getStatus())) {
            throw new ValidationException("Test task must be in PENDING status to start");
        }

        testTask.setStatus(TestTask.Status.RUNNING.name());
        TestTask updated = testTaskRepository.save(testTask);

        log.info("Test task started: {}", id);

        return convertToResponse(updated, null);
    }

    /**
     * Cancel a test task
     */
    @Transactional
    public TestTaskResponse cancelTestTask(UUID id) {
        TestTask testTask = testTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestTask", "id", id));

        if (TestTask.Status.COMPLETED.name().equals(testTask.getStatus())) {
            throw new ValidationException("Cannot cancel a completed test task");
        }

        testTask.setStatus(TestTask.Status.CANCELLED.name());
        TestTask updated = testTaskRepository.save(testTask);

        log.info("Test task cancelled: {}", id);

        return convertToResponse(updated, null);
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
