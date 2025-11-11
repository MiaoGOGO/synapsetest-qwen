package com.synapsetest.testmanagement.controller;

import com.synapsetest.testmanagement.dto.ApiResponse;
import com.synapsetest.testmanagement.model.TestEnvironment;
import com.synapsetest.testmanagement.service.TestEnvironmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * TestEnvironment Controller
 * REST API endpoints for test environment management
 *
 * Task: T036 [US1] Implement TestEnvironmentController
 */
@RestController
@RequestMapping("/api/v1/test-environments")
@RequiredArgsConstructor
public class TestEnvironmentController {

    private final TestEnvironmentService environmentService;

    /**
     * Get all available environments
     * GET /api/v1/test-environments
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TestEnvironment>>> getAvailableEnvironments() {
        List<TestEnvironment> environments = environmentService.getAvailableEnvironments();
        return ResponseEntity.ok(ApiResponse.success(environments));
    }

    /**
     * Get environment by ID
     * GET /api/v1/test-environments/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TestEnvironment>> getEnvironment(@PathVariable UUID id) {
        TestEnvironment environment = environmentService.getEnvironmentById(id);
        return ResponseEntity.ok(ApiResponse.success(environment));
    }

    /**
     * Get environment by name
     * GET /api/v1/test-environments/by-name/{name}
     */
    @GetMapping("/by-name/{name}")
    public ResponseEntity<ApiResponse<TestEnvironment>> getEnvironmentByName(@PathVariable String name) {
        TestEnvironment environment = environmentService.getEnvironmentByName(name);
        return ResponseEntity.ok(ApiResponse.success(environment));
    }
}
