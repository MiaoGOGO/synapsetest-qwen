package com.synapsetest.testmanagement.controller;

import com.synapsetest.testmanagement.constants.ApiVersion;
import com.synapsetest.testmanagement.dto.ApiResponse;
import com.synapsetest.testmanagement.model.TestEnvironment;
import com.synapsetest.testmanagement.service.TestEnvironmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TestEnvironment Controller (MyBatis version)
 * REST API endpoints for test environment management
 *
 * Task: T036 [US1] Implement TestEnvironmentController
 */
@Tag(name = "测试环境管理", description = "测试环境的查询和管理接口")
@RestController
@RequestMapping(ApiVersion.V1 + "/test-environments")
@RequiredArgsConstructor
public class TestEnvironmentController {

    private final TestEnvironmentService environmentService;

    /**
     * Get all available environments
     * GET /api/v1/test-environments
     */
    @Operation(summary = "获取所有可用环境", description = "获取系统中所有可用的测试环境列表")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TestEnvironment>>> getAvailableEnvironments() {
        List<TestEnvironment> environments = environmentService.getAvailableEnvironments();
        return ResponseEntity.ok(ApiResponse.success(environments));
    }

    /**
     * Get environment by ID
     * GET /api/v1/test-environments/{id}
     */
    @Operation(summary = "根据ID获取环境", description = "根据环境ID查询具体的测试环境信息")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TestEnvironment>> getEnvironment(
            @Parameter(description = "环境ID", required = true, example = "env-123456")
            @PathVariable String id) {
        TestEnvironment environment = environmentService.getEnvironmentById(id);
        return ResponseEntity.ok(ApiResponse.success(environment));
    }

    /**
     * Get environment by name
     * GET /api/v1/test-environments/by-name/{name}
     */
    @Operation(summary = "根据名称获取环境", description = "根据环境名称查询测试环境信息")
    @GetMapping("/by-name/{name}")
    public ResponseEntity<ApiResponse<TestEnvironment>> getEnvironmentByName(
            @Parameter(description = "环境名称", required = true, example = "DEV")
            @PathVariable String name) {
        TestEnvironment environment = environmentService.getEnvironmentByName(name);
        return ResponseEntity.ok(ApiResponse.success(environment));
    }
}
