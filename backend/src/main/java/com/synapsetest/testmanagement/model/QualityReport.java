package com.synapsetest.testmanagement.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * QualityReport Model
 * Represents a quality report for test execution results
 * Stored in MongoDB for flexible schema
 *
 * User Story 3: 测试结果可视化分析
 * Task: T061 [P] [US3] Create QualityReport model
 */
@Data
@Document(collection = "quality_reports")
public class QualityReport {

    @Id
    private String id;

    private String taskId; // Reference to TestTask

    private String name;

    private String summary;

    private List<TestResult> testResults;

    private Map<String, Integer> defectStats; // severity -> count

    private Map<String, Object> performanceMetrics;

    private RiskAssessment riskAssessment;

    private LocalDateTime generatedAt;

    private String status; // GENERATING, COMPLETED, ARCHIVED

    /**
     * Test Result inner class
     */
    @Data
    public static class TestResult {
        private String testCaseId;
        private String testCaseName;
        private String status; // PASSED, FAILED, SKIPPED, BLOCKED
        private Long executionTime; // milliseconds
        private String error;
        private String screenshot;
        private LocalDateTime executedAt;
    }

    /**
     * Risk Assessment inner class
     */
    @Data
    public static class RiskAssessment {
        private String overallRisk; // LOW, MEDIUM, HIGH, CRITICAL
        private Double riskScore; // 0.0 - 1.0
        private List<String> highRiskModules;
        private List<String> recommendations;
        private Map<String, Double> moduleRiskScores;
    }

    public enum Status {
        GENERATING, COMPLETED, ARCHIVED
    }

    public enum RiskLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}
