package com.synapsetest.testmanagement.service;

import com.synapsetest.testmanagement.dto.TestCaseRequest;
import com.synapsetest.testmanagement.dto.TestCaseResponse;
import com.synapsetest.testmanagement.exception.ResourceNotFoundException;
import com.synapsetest.testmanagement.model.TestCase;
import com.synapsetest.testmanagement.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * TestCase Service
 * Business logic for test case management
 *
 * Task: T048 [US2] Implement TestCaseService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestCaseService {

    private final TestCaseRepository testCaseRepository;

    /**
     * Create a new test case
     */
    @Transactional
    public TestCaseResponse createTestCase(TestCaseRequest request, String username) {
        log.info("Creating test case: {} by user: {}", request.getTitle(), username);

        TestCase testCase = new TestCase();
        testCase.setTitle(request.getTitle());
        testCase.setDescription(request.getDescription());
        testCase.setSteps(request.getSteps());
        testCase.setExpectedResults(request.getExpectedResults());
        testCase.setPriority(request.getPriority() != null ? request.getPriority() : 0);
        testCase.setType(request.getType());
        testCase.setStatus(TestCase.Status.DRAFT.name());
        testCase.setTags(request.getTags());
        testCase.setRelatedRequirement(request.getRelatedRequirement());
        testCase.setCreatedBy(username);

        TestCase saved = testCaseRepository.save(testCase);

        log.info("Test case created successfully with ID: {}", saved.getId());

        return convertToResponse(saved);
    }

    /**
     * Get test case by ID
     */
    public TestCaseResponse getTestCaseById(UUID id) {
        TestCase testCase = testCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestCase", "id", id));

        return convertToResponse(testCase);
    }

    /**
     * Get all test cases with pagination
     */
    public Page<TestCaseResponse> getAllTestCases(Pageable pageable) {
        return testCaseRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    /**
     * Get test cases by status
     */
    public List<TestCaseResponse> getTestCasesByStatus(String status) {
        return testCaseRepository.findByStatus(status)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get test cases by type
     */
    public List<TestCaseResponse> getTestCasesByType(String type) {
        return testCaseRepository.findByType(type)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update test case
     */
    @Transactional
    public TestCaseResponse updateTestCase(UUID id, TestCaseRequest request) {
        TestCase testCase = testCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestCase", "id", id));

        testCase.setTitle(request.getTitle());
        testCase.setDescription(request.getDescription());
        testCase.setSteps(request.getSteps());
        testCase.setExpectedResults(request.getExpectedResults());
        testCase.setPriority(request.getPriority());
        testCase.setType(request.getType());
        testCase.setTags(request.getTags());
        testCase.setRelatedRequirement(request.getRelatedRequirement());

        TestCase updated = testCaseRepository.save(testCase);

        log.info("Test case updated: {}", id);

        return convertToResponse(updated);
    }

    /**
     * Approve test case
     */
    @Transactional
    public TestCaseResponse approveTestCase(UUID id) {
        TestCase testCase = testCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestCase", "id", id));

        testCase.setStatus(TestCase.Status.APPROVED.name());
        TestCase updated = testCaseRepository.save(testCase);

        log.info("Test case approved: {}", id);

        return convertToResponse(updated);
    }

    /**
     * Delete test case
     */
    @Transactional
    public void deleteTestCase(UUID id) {
        if (!testCaseRepository.existsById(id)) {
            throw new ResourceNotFoundException("TestCase", "id", id);
        }

        testCaseRepository.deleteById(id);
        log.info("Test case deleted: {}", id);
    }

    /**
     * Convert entity to response DTO
     */
    private TestCaseResponse convertToResponse(TestCase testCase) {
        TestCaseResponse response = new TestCaseResponse();
        response.setId(testCase.getId());
        response.setTitle(testCase.getTitle());
        response.setDescription(testCase.getDescription());
        response.setSteps(testCase.getSteps());
        response.setExpectedResults(testCase.getExpectedResults());
        response.setPriority(testCase.getPriority());
        response.setType(testCase.getType());
        response.setStatus(testCase.getStatus());
        response.setTags(testCase.getTags());
        response.setRelatedRequirement(testCase.getRelatedRequirement());
        response.setCreatedAt(testCase.getCreatedAt());
        response.setUpdatedAt(testCase.getUpdatedAt());
        response.setCreatedBy(testCase.getCreatedBy());

        return response;
    }
}
