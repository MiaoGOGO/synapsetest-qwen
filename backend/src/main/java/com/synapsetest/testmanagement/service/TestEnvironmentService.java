package com.synapsetest.testmanagement.service;

import com.synapsetest.testmanagement.exception.ResourceNotFoundException;
import com.synapsetest.testmanagement.model.TestEnvironment;
import com.synapsetest.testmanagement.repository.TestEnvironmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * TestEnvironment Service
 * Business logic for test environment management
 *
 * Task: T031 [US1] Implement TestEnvironmentService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestEnvironmentService {

    private final TestEnvironmentRepository environmentRepository;

    /**
     * Get all available environments
     */
    public List<TestEnvironment> getAvailableEnvironments() {
        return environmentRepository.findByStatus(TestEnvironment.Status.AVAILABLE.name());
    }

    /**
     * Get environment by ID
     */
    public TestEnvironment getEnvironmentById(UUID id) {
        return environmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestEnvironment", "id", id));
    }

    /**
     * Get environment by name
     */
    public TestEnvironment getEnvironmentByName(String name) {
        return environmentRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("TestEnvironment", "name", name));
    }

    /**
     * Create a new environment
     */
    @Transactional
    public TestEnvironment createEnvironment(TestEnvironment environment) {
        log.info("Creating test environment: {}", environment.getName());
        return environmentRepository.save(environment);
    }

    /**
     * Update environment status
     */
    @Transactional
    public TestEnvironment updateEnvironmentStatus(UUID id, String status) {
        TestEnvironment environment = getEnvironmentById(id);
        environment.setStatus(status);
        log.info("Updated environment {} status to {}", id, status);
        return environmentRepository.save(environment);
    }
}
