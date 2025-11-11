package com.synapsetest.testmanagement.model;

import com.synapsetest.testmanagement.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * TestTask Model
 * Represents a test task in the system
 *
 * User Story 1: 智能测试任务调度
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "test_tasks")
public class TestTask extends BaseEntity {

    @NotBlank(message = "Task name is required")
    @Size(max = 100, message = "Task name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Environment is required")
    @Column(name = "environment", nullable = false, length = 50)
    private String environment;

    @NotBlank(message = "Version is required")
    @Column(name = "version", nullable = false, length = 50)
    private String version;

    @Column(name = "test_scope", length = 50)
    private String testScope;

    @NotBlank(message = "Status is required")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Min(value = 0, message = "Priority must be at least 0")
    @Max(value = 10, message = "Priority must not exceed 10")
    @Column(name = "priority", nullable = false)
    private Integer priority = 0;

    @NotBlank(message = "Created by is required")
    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    public enum Status {
        PENDING, RUNNING, COMPLETED, CANCELLED
    }

    public enum Environment {
        DEV, STAGING, PROD
    }
}
