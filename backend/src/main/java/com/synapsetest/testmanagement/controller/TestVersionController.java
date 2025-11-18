package com.synapsetest.testmanagement.controller;

import com.synapsetest.testmanagement.constants.ApiVersion;
import com.synapsetest.testmanagement.dto.ApiResponse;
import com.synapsetest.testmanagement.model.TestVersion;
import com.synapsetest.testmanagement.service.TestVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TestVersion Controller (MyBatis version)
 * REST API endpoints for test version management
 *
 * Task: T037 [US1] Implement TestVersionController
 */
@Tag(name = "测试版本管理", description = "测试版本的查询和管理接口")
@RestController
@RequestMapping(ApiVersion.V1 + "/test-versions")
@RequiredArgsConstructor
public class TestVersionController {

    private final TestVersionService versionService;

    /**
     * Get all versions
     * GET /api/v1/test-versions
     */
    @Operation(summary = "获取所有版本", description = "获取系统中所有测试版本列表")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TestVersion>>> getAllVersions() {
        List<TestVersion> versions = versionService.getAllVersions();
        return ResponseEntity.ok(ApiResponse.success(versions));
    }

    /**
     * Get version by ID
     * GET /api/v1/test-versions/{id}
     */
    @Operation(summary = "根据ID获取版本", description = "根据版本ID查询具体的测试版本信息")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TestVersion>> getVersion(
            @Parameter(description = "版本ID", required = true, example = "version-123456")
            @PathVariable String id) {
        TestVersion version = versionService.getVersionById(id);
        return ResponseEntity.ok(ApiResponse.success(version));
    }

    /**
     * Get versions by product version
     * GET /api/v1/test-versions/by-product/{productVersion}
     */
    @Operation(summary = "根据产品版本获取测试版本", description = "根据产品版本号查询对应的测试版本列表")
    @GetMapping("/by-product/{productVersion}")
    public ResponseEntity<ApiResponse<List<TestVersion>>> getVersionsByProductVersion(
            @Parameter(description = "产品版本号", required = true, example = "v1.0.0")
            @PathVariable String productVersion) {
        List<TestVersion> versions = versionService.getVersionsByProductVersion(productVersion);
        return ResponseEntity.ok(ApiResponse.success(versions));
    }
}
