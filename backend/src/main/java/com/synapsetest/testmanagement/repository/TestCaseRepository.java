package com.synapsetest.testmanagement.repository;

import com.synapsetest.testmanagement.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * TestCase Repository
 */
@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, UUID> {

    List<TestCase> findByType(String type);

    List<TestCase> findByStatus(String status);

    List<TestCase> findByRelatedRequirement(String relatedRequirement);

    List<TestCase> findByCreatedBy(String createdBy);

    List<TestCase> findByStatusOrderByPriorityDesc(String status);
}
