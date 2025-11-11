package com.synapsetest.testmanagement.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for test task response
 */
@Data
public class TestTaskResponse {

    private UUID id;
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
