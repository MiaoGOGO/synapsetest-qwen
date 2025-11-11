package com.synapsetest.testmanagement.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * MonitoringData Model
 * Represents real-time monitoring metrics
 * Stored in MongoDB for time-series data
 *
 * User Story 3: 测试结果可视化分析
 * Task: T062 [P] [US3] Create MonitoringData model
 */
@Data
@Document(collection = "monitoring_data")
public class MonitoringData {

    @Id
    private String id;

    private String taskId; // Reference to TestTask

    private String status; // PENDING, RUNNING, COMPLETED, FAILED

    private Integer progress; // 0-100

    private Integer executedCases;

    private Integer totalCases;

    private Integer passedCases;

    private Integer failedCases;

    private Integer skippedCases;

    private LocalDateTime startTime;

    private LocalDateTime estimatedEndTime;

    private LocalDateTime actualEndTime;

    private Map<String, Object> resourceUsage; // CPU, Memory, etc.

    private Map<String, Object> performanceMetrics; // Response time, Throughput, etc.

    private LocalDateTime timestamp;

    private String environment;

    private String version;

    /**
     * Calculate pass rate
     */
    public Double getPassRate() {
        if (executedCases == null || executedCases == 0) {
            return 0.0;
        }
        return (double) (passedCases != null ? passedCases : 0) / executedCases * 100;
    }

    /**
     * Calculate remaining time in minutes
     */
    public Long getRemainingMinutes() {
        if (estimatedEndTime == null || actualEndTime != null) {
            return 0L;
        }
        LocalDateTime now = LocalDateTime.now();
        return java.time.Duration.between(now, estimatedEndTime).toMinutes();
    }

    /**
     * Check if task is completed
     */
    public boolean isCompleted() {
        return "COMPLETED".equals(status) || "FAILED".equals(status);
    }

    public enum Status {
        PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
    }
}
