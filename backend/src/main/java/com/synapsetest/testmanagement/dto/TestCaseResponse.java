package com.synapsetest.testmanagement.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for test case response
 */
@Data
public class TestCaseResponse {

    private UUID id;
    private String title;
    private String description;
    private List<String> steps;
    private String expectedResults;
    private Integer priority;
    private String type;
    private String status;
    private List<String> tags;
    private String relatedRequirement;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
}
