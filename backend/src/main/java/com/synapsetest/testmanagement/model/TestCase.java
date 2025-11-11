package com.synapsetest.testmanagement.model;

import com.synapsetest.testmanagement.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

import java.util.List;

/**
 * TestCase Model
 * Represents a test case in the system
 *
 * User Story 2: AI生成测试用例
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "test_cases")
public class TestCase extends BaseEntity {

    @NotBlank(message = "Test case title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Type(type = "jsonb")
    @Column(name = "steps", columnDefinition = "jsonb", nullable = false)
    private List<String> steps;

    @Column(name = "expected_results", columnDefinition = "TEXT")
    private String expectedResults;

    @Min(value = 0, message = "Priority must be at least 0")
    @Max(value = 10, message = "Priority must not exceed 10")
    @Column(name = "priority", nullable = false)
    private Integer priority = 0;

    @NotBlank(message = "Type is required")
    @Column(name = "type", nullable = false, length = 20)
    private String type; // FUNCTIONAL, PERFORMANCE, SECURITY

    @NotBlank(message = "Status is required")
    @Column(name = "status", nullable = false, length = 20)
    private String status; // DRAFT, APPROVED, DEPRECATED

    @Type(type = "jsonb")
    @Column(name = "tags", columnDefinition = "jsonb")
    private List<String> tags;

    @Column(name = "related_requirement", length = 200)
    private String relatedRequirement;

    @NotBlank(message = "Created by is required")
    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    public enum Type {
        FUNCTIONAL, PERFORMANCE, SECURITY
    }

    public enum Status {
        DRAFT, APPROVED, DEPRECATED
    }
}
