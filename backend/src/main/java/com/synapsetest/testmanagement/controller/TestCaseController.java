package com.synapsetest.testmanagement.controller;

import com.synapsetest.testmanagement.dto.*;
import com.synapsetest.testmanagement.service.AITestCaseGenerationService;
import com.synapsetest.testmanagement.service.AITestCaseOptimizationService;
import com.synapsetest.testmanagement.service.TestCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * TestCase Controller
 * REST API endpoints for test case management
 *
 * Task: T050 [US2] Implement TestCaseController
 */
@RestController
@RequestMapping("/api/v1/test-cases")
@RequiredArgsConstructor
public class TestCaseController {

    private final TestCaseService testCaseService;
    private final AITestCaseGenerationService aiGenerationService;
    private final AITestCaseOptimizationService aiOptimizationService;

    /**
     * AI Generate test cases
     * POST /api/v1/test-cases/generate
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateTestCases(
            @Valid @RequestBody AITestCaseGenerationRequest request) {

        List<TestCaseResponse> generatedCases = aiGenerationService.generateTestCases(request);
        Double confidenceScore = aiGenerationService.calculateConfidenceScore(request);

        Map<String, Object> result = Map.of(
                "testCases", generatedCases,
                "confidenceScore", confidenceScore,
                "count", generatedCases.size()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Test cases generated successfully", result));
    }

    /**
     * Create a new test case
     * POST /api/v1/test-cases
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TestCaseResponse>> createTestCase(
            @Valid @RequestBody TestCaseRequest request,
            @RequestHeader(value = "X-User-Name", defaultValue = "system") String username) {

        TestCaseResponse response = testCaseService.createTestCase(request, username);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Test case created successfully", response));
    }

    /**
     * Get test case by ID
     * GET /api/v1/test-cases/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TestCaseResponse>> getTestCase(@PathVariable UUID id) {
        TestCaseResponse response = testCaseService.getTestCaseById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all test cases with pagination
     * GET /api/v1/test-cases
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TestCaseResponse>>> getAllTestCases(Pageable pageable) {
        Page<TestCaseResponse> response = testCaseService.getAllTestCases(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get test cases by status
     * GET /api/v1/test-cases?status=DRAFT
     */
    @GetMapping(params = "status")
    public ResponseEntity<ApiResponse<List<TestCaseResponse>>> getTestCasesByStatus(
            @RequestParam String status) {
        List<TestCaseResponse> response = testCaseService.getTestCasesByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get test cases by type
     * GET /api/v1/test-cases?type=FUNCTIONAL
     */
    @GetMapping(params = "type")
    public ResponseEntity<ApiResponse<List<TestCaseResponse>>> getTestCasesByType(
            @RequestParam String type) {
        List<TestCaseResponse> response = testCaseService.getTestCasesByType(type);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Update test case
     * PUT /api/v1/test-cases/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TestCaseResponse>> updateTestCase(
            @PathVariable UUID id,
            @Valid @RequestBody TestCaseRequest request) {

        TestCaseResponse response = testCaseService.updateTestCase(id, request);
        return ResponseEntity.ok(ApiResponse.success("Test case updated successfully", response));
    }

    /**
     * Approve test case
     * POST /api/v1/test-cases/{id}/approve
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<TestCaseResponse>> approveTestCase(@PathVariable UUID id) {
        TestCaseResponse response = testCaseService.approveTestCase(id);
        return ResponseEntity.ok(ApiResponse.success("Test case approved", response));
    }

    /**
     * Delete test case
     * DELETE /api/v1/test-cases/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTestCase(@PathVariable UUID id) {
        testCaseService.deleteTestCase(id);
        return ResponseEntity.ok(ApiResponse.success("Test case deleted successfully", null));
    }

    /**
     * Deduplicate test cases
     * POST /api/v1/test-cases/deduplicate
     */
    @PostMapping("/deduplicate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deduplicateTestCases(
            @RequestBody List<TestCaseResponse> testCases) {

        List<TestCaseResponse> optimized = aiOptimizationService.deduplicateTestCases(testCases);

        Map<String, Object> result = Map.of(
                "original", testCases.size(),
                "optimized", optimized.size(),
                "reduction", String.format("%.1f%%", (1 - (double) optimized.size() / testCases.size()) * 100),
                "testCases", optimized
        );

        return ResponseEntity.ok(ApiResponse.success("Test cases deduplicated", result));
    }

    /**
     * Analyze test coverage
     * POST /api/v1/test-cases/analyze-coverage
     */
    @PostMapping("/analyze-coverage")
    public ResponseEntity<ApiResponse<Map<String, Object>>> analyzeTestCoverage(
            @RequestBody List<TestCaseResponse> testCases) {

        Map<String, Object> coverage = aiOptimizationService.analyzeTestCoverage(testCases);

        return ResponseEntity.ok(ApiResponse.success("Test coverage analyzed", coverage));
    }
}
