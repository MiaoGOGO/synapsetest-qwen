package com.synapsetest.testmanagement.service;

import com.synapsetest.testmanagement.dto.TestCaseRequest;
import com.synapsetest.testmanagement.dto.TestCaseResponse;
import com.synapsetest.testmanagement.exception.ResourceNotFoundException;
import com.synapsetest.testmanagement.mapper.TestCaseMapper;
import com.synapsetest.testmanagement.model.TestCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * TestCase Service
 * Business logic for test case management (MyBatis version)
 *
 * Task: T048 [US2] Implement TestCaseService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestCaseService {

    private final TestCaseMapper testCaseMapper;

    /**
     * Create a new test case
     */
    public TestCaseResponse createTestCase(TestCaseRequest request, String username) {
        log.info("Creating test case: {} by user: {}", request.getTitle(), username);

        TestCase testCase = new TestCase();
        testCase.setId(UUID.randomUUID().toString());
        testCase.setTitle(request.getTitle());
        testCase.setDescription(request.getDescription());
        testCase.setSteps(request.getSteps());
        testCase.setExpectedResult(request.getExpectedResults());
        testCase.setPriority(request.getPriority() != null ? request.getPriority() : 0);
        testCase.setType(request.getType());
        testCase.setStatus(TestCase.TestCaseStatus.DRAFT.name());
        testCase.setTags(request.getTags());
        testCase.setRelatedRequirement(request.getRelatedRequirement());
        testCase.setCreatedBy(username);
        testCase.setCreatedAt(LocalDateTime.now());
        testCase.setUpdatedAt(LocalDateTime.now());

        testCaseMapper.insert(testCase);

        log.info("Test case created successfully with ID: {}", testCase.getId());

        return convertToResponse(testCase);
    }

    /**
     * Get test case by ID
     */
    public TestCaseResponse getTestCaseById(String id) {
        TestCase testCase = testCaseMapper.selectById(id);
        if (testCase == null) {
            throw new ResourceNotFoundException("TestCase", "id", id);
        }

        return convertToResponse(testCase);
    }

    /**
     * Get all test cases
     */
    public List<TestCaseResponse> getAllTestCases() {
        return testCaseMapper.selectAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get test cases by status
     */
    public List<TestCaseResponse> getTestCasesByStatus(String status) {
        return testCaseMapper.selectByStatus(status)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get test cases by type
     */
    public List<TestCaseResponse> getTestCasesByType(String type) {
        return testCaseMapper.selectByType(type)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update test case
     */
    public TestCaseResponse updateTestCase(String id, TestCaseRequest request) {
        TestCase testCase = testCaseMapper.selectById(id);
        if (testCase == null) {
            throw new ResourceNotFoundException("TestCase", "id", id);
        }

        testCase.setTitle(request.getTitle());
        testCase.setDescription(request.getDescription());
        testCase.setSteps(request.getSteps());
        testCase.setExpectedResult(request.getExpectedResults());
        testCase.setPriority(request.getPriority());
        testCase.setType(request.getType());
        testCase.setTags(request.getTags());
        testCase.setRelatedRequirement(request.getRelatedRequirement());
        testCase.setUpdatedAt(LocalDateTime.now());

        testCaseMapper.update(testCase);

        log.info("Test case updated: {}", id);

        return convertToResponse(testCase);
    }

    /**
     * Approve test case
     */
    public TestCaseResponse approveTestCase(String id) {
        TestCase testCase = testCaseMapper.selectById(id);
        if (testCase == null) {
            throw new ResourceNotFoundException("TestCase", "id", id);
        }

        testCase.setStatus(TestCase.TestCaseStatus.APPROVED.name());
        testCase.setUpdatedAt(LocalDateTime.now());
        testCaseMapper.update(testCase);

        log.info("Test case approved: {}", id);

        return convertToResponse(testCase);
    }

    /**
     * Delete test case
     */
    public void deleteTestCase(String id) {
        TestCase testCase = testCaseMapper.selectById(id);
        if (testCase == null) {
            throw new ResourceNotFoundException("TestCase", "id", id);
        }

        testCaseMapper.deleteById(id);
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
        response.setExpectedResults(testCase.getExpectedResult());
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
