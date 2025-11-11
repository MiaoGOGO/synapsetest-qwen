package com.synapsetest.testmanagement.controller;

import com.synapsetest.testmanagement.dto.ApiResponse;
import com.synapsetest.testmanagement.dto.TestTaskRequest;
import com.synapsetest.testmanagement.dto.TestTaskResponse;
import com.synapsetest.testmanagement.service.TestTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * TestTask Controller
 * REST API endpoints for test task management
 *
 * Task: T035 [US1] Implement TestTaskController
 */
@RestController
@RequestMapping("/api/v1/test-tasks")
@RequiredArgsConstructor
public class TestTaskController {

    private final TestTaskService testTaskService;

    /**
     * Create a new test task
     * POST /api/v1/test-tasks
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TestTaskResponse>> createTestTask(
            @Valid @RequestBody TestTaskRequest request,
            @RequestHeader(value = "X-User-Name", defaultValue = "system") String username) {

        TestTaskResponse response = testTaskService.createTestTask(request, username);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Test task created successfully", response));
    }

    /**
     * Get test task by ID
     * GET /api/v1/test-tasks/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TestTaskResponse>> getTestTask(@PathVariable UUID id) {
        TestTaskResponse response = testTaskService.getTestTaskById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all test tasks with pagination
     * GET /api/v1/test-tasks
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TestTaskResponse>>> getAllTestTasks(Pageable pageable) {
        Page<TestTaskResponse> response = testTaskService.getAllTestTasks(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get test tasks by status
     * GET /api/v1/test-tasks?status=PENDING
     */
    @GetMapping(params = "status")
    public ResponseEntity<ApiResponse<List<TestTaskResponse>>> getTestTasksByStatus(
            @RequestParam String status) {
        List<TestTaskResponse> response = testTaskService.getTestTasksByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Start a test task
     * POST /api/v1/test-tasks/{id}/start
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResponse<TestTaskResponse>> startTestTask(@PathVariable UUID id) {
        TestTaskResponse response = testTaskService.startTestTask(id);
        return ResponseEntity.ok(ApiResponse.success("Test task started", response));
    }

    /**
     * Cancel a test task
     * POST /api/v1/test-tasks/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<TestTaskResponse>> cancelTestTask(@PathVariable UUID id) {
        TestTaskResponse response = testTaskService.cancelTestTask(id);
        return ResponseEntity.ok(ApiResponse.success("Test task cancelled", response));
    }
}
