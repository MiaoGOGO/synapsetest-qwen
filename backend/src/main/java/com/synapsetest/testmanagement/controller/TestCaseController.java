package com.synapsetest.testmanagement.controller;

import com.synapsetest.testmanagement.constants.ApiVersion;
import com.synapsetest.testmanagement.dto.*;
import com.synapsetest.testmanagement.dto.response.TestCaseResponse;
import com.synapsetest.testmanagement.service.AITestCaseGenerationService;
import com.synapsetest.testmanagement.service.AITestCaseOptimizationService;
import com.synapsetest.testmanagement.service.TestCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * TestCase Controller (MyBatis version)
 * REST API endpoints for test case management
 * AI features require mongodb profile to be active
 *
 * Task: T050 [US2] Implement TestCaseController
 */
@RestController
@RequestMapping(ApiVersion.V1 + "/test-cases")
public class TestCaseController {

    private final TestCaseService testCaseService;
    
    @Autowired(required = false)
    private AITestCaseGenerationService aiGenerationService;
    
    @Autowired(required = false)
    private AITestCaseOptimizationService aiOptimizationService;

    public TestCaseController(TestCaseService testCaseService) {
        this.testCaseService = testCaseService;
    }

    /**
     * AI Generate test cases
     * POST /api/v1/test-cases/generate
     * Requires mongodb profile to be active
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateTestCases(
            @Valid @RequestBody AITestCaseGenerationRequest request) {

        if (aiGenerationService == null) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.error("AI generation service is not available. Please activate mongodb profile."));
        }

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
    public ResponseEntity<ApiResponse<TestCaseResponse>> getTestCase(@PathVariable String id) {
        TestCaseResponse response = testCaseService.getTestCaseById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all test cases
     * GET /api/v1/test-cases
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TestCaseResponse>>> getAllTestCases() {
        List<TestCaseResponse> response = testCaseService.getAllTestCases();
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
            @PathVariable String id,
            @Valid @RequestBody TestCaseRequest request) {

        TestCaseResponse response = testCaseService.updateTestCase(id, request);
        return ResponseEntity.ok(ApiResponse.success("Test case updated successfully", response));
    }

    /**
     * Approve test case
     * POST /api/v1/test-cases/{id}/approve
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<TestCaseResponse>> approveTestCase(@PathVariable String id) {
        TestCaseResponse response = testCaseService.approveTestCase(id);
        return ResponseEntity.ok(ApiResponse.success("Test case approved", response));
    }

    /**
     * Delete test case
     * DELETE /api/v1/test-cases/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTestCase(@PathVariable String id) {
        testCaseService.deleteTestCase(id);
        return ResponseEntity.ok(ApiResponse.success("Test case deleted successfully", null));
    }

    /**
     * Deduplicate test cases
     * POST /api/v1/test-cases/deduplicate
     * Requires mongodb profile to be active
     */
    @PostMapping("/deduplicate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deduplicateTestCases(
            @RequestBody List<TestCaseResponse> testCases) {

        if (aiOptimizationService == null) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.error("AI optimization service is not available. Please activate mongodb profile."));
        }

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
     * Requires mongodb profile to be active
     */
    @PostMapping("/analyze-coverage")
    public ResponseEntity<ApiResponse<Map<String, Object>>> analyzeTestCoverage(
            @RequestBody List<TestCaseResponse> testCases) {

        if (aiOptimizationService == null) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.error("AI optimization service is not available. Please activate mongodb profile."));
        }

        Map<String, Object> coverage = aiOptimizationService.analyzeTestCoverage(testCases);

        return ResponseEntity.ok(ApiResponse.success("Test coverage analyzed", coverage));
    }
}
