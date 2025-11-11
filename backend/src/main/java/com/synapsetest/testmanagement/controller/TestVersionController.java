package com.synapsetest.testmanagement.controller;

import com.synapsetest.testmanagement.dto.ApiResponse;
import com.synapsetest.testmanagement.model.TestVersion;
import com.synapsetest.testmanagement.service.TestVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * TestVersion Controller
 * REST API endpoints for test version management
 *
 * Task: T037 [US1] Implement TestVersionController
 */
@RestController
@RequestMapping("/api/v1/test-versions")
@RequiredArgsConstructor
public class TestVersionController {

    private final TestVersionService versionService;

    /**
     * Get all versions
     * GET /api/v1/test-versions
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TestVersion>>> getAllVersions() {
        List<TestVersion> versions = versionService.getAllVersions();
        return ResponseEntity.ok(ApiResponse.success(versions));
    }

    /**
     * Get version by ID
     * GET /api/v1/test-versions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TestVersion>> getVersion(@PathVariable UUID id) {
        TestVersion version = versionService.getVersionById(id);
        return ResponseEntity.ok(ApiResponse.success(version));
    }

    /**
     * Get versions by product version
     * GET /api/v1/test-versions/by-product/{productVersion}
     */
    @GetMapping("/by-product/{productVersion}")
    public ResponseEntity<ApiResponse<List<TestVersion>>> getVersionsByProductVersion(
            @PathVariable String productVersion) {
        List<TestVersion> versions = versionService.getVersionsByProductVersion(productVersion);
        return ResponseEntity.ok(ApiResponse.success(versions));
    }

    /**
     * Get baseline version
     * GET /api/v1/test-versions/baseline/{productVersion}
     */
    @GetMapping("/baseline/{productVersion}")
    public ResponseEntity<ApiResponse<TestVersion>> getBaselineVersion(
            @PathVariable String productVersion) {
        TestVersion version = versionService.getBaselineVersion(productVersion);
        return ResponseEntity.ok(ApiResponse.success(version));
    }
}
