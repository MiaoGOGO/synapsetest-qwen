package com.synapsetest.testmanagement.service;

import com.synapsetest.testmanagement.exception.ResourceNotFoundException;
import com.synapsetest.testmanagement.model.TestVersion;
import com.synapsetest.testmanagement.repository.TestVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * TestVersion Service
 * Business logic for test version management
 *
 * Task: T032 [US1] Implement TestVersionService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestVersionService {

    private final TestVersionRepository versionRepository;

    /**
     * Get all versions
     */
    public List<TestVersion> getAllVersions() {
        return versionRepository.findAll();
    }

    /**
     * Get version by ID
     */
    public TestVersion getVersionById(UUID id) {
        return versionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestVersion", "id", id));
    }

    /**
     * Get version by name
     */
    public TestVersion getVersionByName(String name) {
        return versionRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("TestVersion", "name", name));
    }

    /**
     * Get versions by product version
     */
    public List<TestVersion> getVersionsByProductVersion(String productVersion) {
        return versionRepository.findByProductVersion(productVersion);
    }

    /**
     * Create a new version
     */
    @Transactional
    public TestVersion createVersion(TestVersion version) {
        log.info("Creating test version: {}", version.getName());
        return versionRepository.save(version);
    }

    /**
     * Get baseline version for a product version
     */
    public TestVersion getBaselineVersion(String productVersion) {
        List<TestVersion> versions = versionRepository.findByProductVersion(productVersion);
        return versions.stream()
                .filter(v -> v.getBaselineVersion() == null)
                .findFirst()
                .orElse(null);
    }
}
