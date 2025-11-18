package com.synapsetest.testmanagement.controller;

import com.synapsetest.testmanagement.constants.ApiVersion;
import com.synapsetest.testmanagement.dto.ApiResponse;
import com.synapsetest.testmanagement.dto.TestTaskRequest;
import com.synapsetest.testmanagement.dto.response.TestTaskResponse;
import com.synapsetest.testmanagement.service.TestTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * TestTask Controller (MyBatis version)
 * REST API endpoints for test task management
 *
 * Task: T035 [US1] Implement TestTaskController
 */
@RestController
@RequestMapping(ApiVersion.V1 + "/test-tasks")
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
    public ResponseEntity<ApiResponse<TestTaskResponse>> getTestTask(@PathVariable String id) {
        TestTaskResponse response = testTaskService.getTestTaskById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all test tasks
     * GET /api/v1/test-tasks
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TestTaskResponse>>> getAllTestTasks() {
        List<TestTaskResponse> response = testTaskService.getAllTestTasks();
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
    public ResponseEntity<ApiResponse<TestTaskResponse>> startTestTask(@PathVariable String id) {
        TestTaskResponse response = testTaskService.startTestTask(id);
        return ResponseEntity.ok(ApiResponse.success("Test task started", response));
    }

    /**
     * Cancel a test task
     * POST /api/v1/test-tasks/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<TestTaskResponse>> cancelTestTask(@PathVariable String id) {
        TestTaskResponse response = testTaskService.cancelTestTask(id);
        return ResponseEntity.ok(ApiResponse.success("Test task cancelled", response));
    }
}
