package com.synapsetest.testmanagement.model;

import com.synapsetest.testmanagement.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Type;

import java.util.Map;

/**
 * TestEnvironment Model
 * Represents a test environment configuration
 *
 * User Story 1: 智能测试任务调度
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "test_environments")
public class TestEnvironment extends BaseEntity {

    @NotBlank(message = "Environment name is required")
    @Size(max = 100, message = "Environment name must not exceed 100 characters")
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Type(type = "jsonb")
    @Column(name = "config", columnDefinition = "jsonb")
    private Map<String, String> config;

    @NotBlank(message = "Status is required")
    @Column(name = "status", nullable = false, length = 20)
    private String status; // AVAILABLE, MAINTENANCE, UNAVAILABLE

    public enum Status {
        AVAILABLE, MAINTENANCE, UNAVAILABLE
    }
}
