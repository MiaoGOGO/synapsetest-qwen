package com.synapsetest.testmanagement.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for test case response (MyBatis version - using String for ID)
 */
@Data
public class TestCaseResponse {

    private String id;
    private String title;
    private String caseName;  // Alias for title, used in some API responses
    private String description;
    private List<String> steps;
    private String expectedResults;
    private Integer priority;  // Priority level (0-10)
    private String type;
    private String status;
    private List<String> tags;
    private String relatedRequirement;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
}

