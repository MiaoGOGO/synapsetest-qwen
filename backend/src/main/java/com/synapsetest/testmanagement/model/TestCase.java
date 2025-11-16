package com.synapsetest.testmanagement.model;

import com.synapsetest.testmanagement.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.util.List;

/**
 * TestCase Model
 * Represents a test case in the system
 * MyBatis POJO (removed JPA annotations)
 *
 * User Story 2: AI生成测试用例
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TestCase extends BaseEntity {

    @NotBlank(message = "Test case title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    private String description;

    private List<String> steps;

    private String expectedResult;

    @Min(value = 0, message = "Priority must be at least 0")
    @Max(value = 10, message = "Priority must not exceed 10")
    private Integer priority = 0;

    @NotBlank(message = "Type is required")
    private String type; // FUNCTIONAL, PERFORMANCE, SECURITY

    @NotBlank(message = "Status is required")
    private String status; // DRAFT, APPROVED, DEPRECATED

    private List<String> tags;

    private String relatedRequirement;

    @NotBlank(message = "Created by is required")
    private String createdBy;

    public enum TestCaseType {
        FUNCTIONAL, PERFORMANCE, SECURITY
    }

    public enum TestCaseStatus {
        DRAFT, APPROVED, DEPRECATED
    }
}
