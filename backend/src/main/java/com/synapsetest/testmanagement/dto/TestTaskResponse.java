package com.synapsetest.testmanagement.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for test task response (MyBatis version - using String for ID)
 */
@Data
public class TestTaskResponse {

    private String id;
    private String name;
    private String description;
    private String environment;
    private String version;
    private String testScope;
    private String status;
    private Integer priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private TestRecommendation recommendation;

    @Data
    public static class TestRecommendation {
        private String recommendedEnvironment;
        private String recommendedVersion;
        private String recommendedScope;
        private Double confidenceScore;
        private String reasoning;
    }
}
