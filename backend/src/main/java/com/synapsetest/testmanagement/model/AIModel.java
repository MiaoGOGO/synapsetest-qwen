package com.synapsetest.testmanagement.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * AIModel Model
 * Represents an AI model with security audit status
 * Stored in MongoDB for flexible schema
 *
 * User Story 2: AI生成测试用例
 */
@Data
@Document(collection = "ai_models")
public class AIModel {

    @Id
    private String id;

    private String name;

    private String version;

    private String description;

    private String filePath;

    private String securityStatus; // PENDING, IN_REVIEW, APPROVED, REJECTED

    private String vulnerabilityScanResult;

    private LocalDateTime lastScanTime;

    private String complianceStatus; // COMPLIANT, NON_COMPLIANT, PENDING

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Map<String, Object> metrics; // Model performance metrics

    public enum SecurityStatus {
        PENDING, IN_REVIEW, APPROVED, REJECTED
    }

    public enum ComplianceStatus {
        COMPLIANT, NON_COMPLIANT, PENDING
    }
}
