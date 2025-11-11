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
 * TestVersion Model
 * Represents a test version configuration
 *
 * User Story 1: 智能测试任务调度
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "test_versions")
public class TestVersion extends BaseEntity {

    @NotBlank(message = "Version name is required")
    @Size(max = 100, message = "Version name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Product version is required")
    @Column(name = "product_version", nullable = false, length = 50)
    private String productVersion;

    @Column(name = "baseline_version", length = 50)
    private String baselineVersion;

    @Type(type = "jsonb")
    @Column(name = "config", columnDefinition = "jsonb")
    private Map<String, String> config;
}
