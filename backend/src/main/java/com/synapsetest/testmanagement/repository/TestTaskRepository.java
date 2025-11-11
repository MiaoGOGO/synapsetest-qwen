package com.synapsetest.testmanagement.repository;

import com.synapsetest.testmanagement.model.TestTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * TestTask Repository
 */
@Repository
public interface TestTaskRepository extends JpaRepository<TestTask, UUID> {

    List<TestTask> findByStatus(String status);

    List<TestTask> findByEnvironment(String environment);

    List<TestTask> findByCreatedBy(String createdBy);

    List<TestTask> findByStatusOrderByPriorityDesc(String status);
}
