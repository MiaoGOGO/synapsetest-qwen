package com.synapsetest.testmanagement.controller;

import com.synapsetest.testmanagement.dto.ApiResponse;
import com.synapsetest.testmanagement.model.QualityReport;
import com.synapsetest.testmanagement.service.QualityReportService;
import com.synapsetest.testmanagement.service.QualityTraceabilityService;
import com.synapsetest.testmanagement.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Report Controller
 * REST API endpoints for quality reports and traceability
 *
 * Task: T067 [US3] Implement ReportController
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final QualityReportService qualityReportService;
    private final ReportingService reportingService;
    private final QualityTraceabilityService traceabilityService;

    /**
     * Get quality report by ID
     * GET /api/v1/reports/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QualityReport>> getReport(@PathVariable String id) {
        QualityReport report = qualityReportService.getReportById(id);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    /**
     * Get quality report by task ID
     * GET /api/v1/reports/task/{taskId}
     */
    @GetMapping("/task/{taskId}")
    public ResponseEntity<ApiResponse<QualityReport>> getReportByTaskId(@PathVariable String taskId) {
        QualityReport report = qualityReportService.getReportByTaskId(taskId);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    /**
     * Get recent reports
     * GET /api/v1/reports/recent
     */
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<QualityReport>>> getRecentReports() {
        List<QualityReport> reports = qualityReportService.getRecentReports();
        return ResponseEntity.ok(ApiResponse.success(reports));
    }

    /**
     * Generate comprehensive report
     * GET /api/v1/reports/comprehensive/{taskId}
     */
    @GetMapping("/comprehensive/{taskId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getComprehensiveReport(
            @PathVariable String taskId) {
        Map<String, Object> report = reportingService.generateComprehensiveReport(taskId);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    /**
     * Generate comparison report
     * GET /api/v1/reports/compare
     */
    @GetMapping("/compare")
    public ResponseEntity<ApiResponse<Map<String, Object>>> compareReports(
            @RequestParam String taskId1,
            @RequestParam String taskId2) {
        Map<String, Object> comparison = reportingService.generateComparisonReport(taskId1, taskId2);
        return ResponseEntity.ok(ApiResponse.success(comparison));
    }

    /**
     * Generate HTML report
     * GET /api/v1/reports/html/{taskId}
     */
    @GetMapping("/html/{taskId}")
    public ResponseEntity<String> getHtmlReport(@PathVariable String taskId) {
        String htmlReport = reportingService.generateHtmlReport(taskId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);

        return ResponseEntity.ok()
                .headers(headers)
                .body(htmlReport);
    }

    /**
     * Trace requirement coverage
     * GET /api/v1/reports/traceability/requirement/{requirementId}
     */
    @GetMapping("/traceability/requirement/{requirementId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> traceRequirementCoverage(
            @PathVariable String requirementId) {
        Map<String, Object> traceability = traceabilityService.traceRequirementCoverage(requirementId);
        return ResponseEntity.ok(ApiResponse.success(traceability));
    }

    /**
     * Generate traceability matrix
     * POST /api/v1/reports/traceability/matrix
     */
    @PostMapping("/traceability/matrix")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateTraceabilityMatrix(
            @RequestBody List<String> requirementIds) {
        Map<String, Object> matrix = traceabilityService.generateTraceabilityMatrix(requirementIds);
        return ResponseEntity.ok(ApiResponse.success(matrix));
    }

    /**
     * Check release quality gates
     * GET /api/v1/reports/quality-gates/{taskId}
     */
    @GetMapping("/quality-gates/{taskId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkQualityGates(
            @PathVariable String taskId) {
        Map<String, Object> gateCheck = traceabilityService.checkReleaseQualityGates(taskId);
        return ResponseEntity.ok(ApiResponse.success(gateCheck));
    }

    /**
     * Trace defect impact
     * GET /api/v1/reports/traceability/defect/{defectId}
     */
    @GetMapping("/traceability/defect/{defectId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> traceDefectImpact(
            @PathVariable String defectId) {
        Map<String, Object> impact = traceabilityService.traceDefectImpact(defectId);
        return ResponseEntity.ok(ApiResponse.success(impact));
    }

    /**
     * Analyze change impact
     * POST /api/v1/reports/traceability/change-impact
     */
    @PostMapping("/traceability/change-impact")
    public ResponseEntity<ApiResponse<Map<String, Object>>> analyzeChangeImpact(
            @RequestParam String changeId,
            @RequestBody List<String> changedFiles) {
        Map<String, Object> analysis = traceabilityService.analyzeChangeImpact(changeId, changedFiles);
        return ResponseEntity.ok(ApiResponse.success(analysis));
    }
}
