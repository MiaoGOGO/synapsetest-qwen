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

    // Use standalone TestResult class
    private List<TestResult> testResults;

    private Map<String, Integer> defectStats; // severity -> count

    private Map<String, Object> performanceMetrics;

    // Use standalone RiskAssessment class
    private RiskAssessment riskAssessment;

    private LocalDateTime generatedAt;

    private String status; // GENERATING, COMPLETED, ARCHIVED

    public enum Status {
        GENERATING, COMPLETED, ARCHIVED
    }

    public enum RiskLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}
