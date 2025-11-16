package com.synapsetest.testmanagement.service;

import com.synapsetest.testmanagement.exception.ResourceNotFoundException;
import com.synapsetest.testmanagement.mapper.TestVersionMapper;
import com.synapsetest.testmanagement.model.TestVersion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * TestVersion Service (MyBatis version)
 * Business logic for test version management
 *
 * Task: T032 [US1] Implement TestVersionService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestVersionService {

    private final TestVersionMapper versionMapper;

    /**
     * Get all versions
     */
    public List<TestVersion> getAllVersions() {
        return versionMapper.selectAll();
    }

    /**
     * Get version by ID
     */
    public TestVersion getVersionById(String id) {
        TestVersion version = versionMapper.selectById(id);
        if (version == null) {
            throw new ResourceNotFoundException("TestVersion", "id", id);
        }
        return version;
    }

    /**
     * Get version by name
     */
    public TestVersion getVersionByName(String name) {
        TestVersion version = versionMapper.selectByName(name);
        if (version == null) {
            throw new ResourceNotFoundException("TestVersion", "name", name);
        }
        return version;
    }

    /**
     * Get versions by product version
     */
    public List<TestVersion> getVersionsByProductVersion(String productVersion) {
        return versionMapper.selectByProductVersion(productVersion);
    }

    /**
     * Create a new version
     */
    public TestVersion createVersion(TestVersion version) {
        log.info("Creating test version: {}", version.getName());
        version.setId(UUID.randomUUID().toString());
        version.setCreatedAt(LocalDateTime.now());
        version.setUpdatedAt(LocalDateTime.now());
        versionMapper.insert(version);
        return version;
    }
}
