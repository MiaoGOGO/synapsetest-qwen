package com.synapsetest.testmanagement.model;

import com.synapsetest.testmanagement.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

import java.util.Map;

/**
 * ResourcePool Model
 * Represents a pool of test resources
 *
 * User Story 1: 智能测试任务调度
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "resource_pools")
public class ResourcePool extends BaseEntity {

    @NotBlank(message = "Resource pool name is required")
    @Size(max = 100, message = "Resource pool name must not exceed 100 characters")
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Type is required")
    @Column(name = "type", nullable = false, length = 20)
    private String type; // VM, CONTAINER, DEVICE

    @Min(value = 1, message = "Capacity must be at least 1")
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Min(value = 0, message = "Used must be at least 0")
    @Column(name = "used", nullable = false)
    private Integer used = 0;

    @Type(type = "jsonb")
    @Column(name = "config", columnDefinition = "jsonb")
    private Map<String, String> config;

    @NotBlank(message = "Status is required")
    @Column(name = "status", nullable = false, length = 20)
    private String status; // AVAILABLE, MAINTENANCE, UNAVAILABLE

    public enum Type {
        VM, CONTAINER, DEVICE
    }

    public enum Status {
        AVAILABLE, MAINTENANCE, UNAVAILABLE
    }

    /**
     * Check if resources are available
     */
    public boolean hasAvailableResources() {
        return used < capacity;
    }

    /**
     * Get available resources count
     */
    public int getAvailableResources() {
        return capacity - used;
    }
}
