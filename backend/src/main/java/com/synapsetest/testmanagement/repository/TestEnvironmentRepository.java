package com.synapsetest.testmanagement.repository;

import com.synapsetest.testmanagement.model.TestEnvironment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * TestEnvironment Repository
 */
@Repository
public interface TestEnvironmentRepository extends JpaRepository<TestEnvironment, UUID> {

    Optional<TestEnvironment> findByName(String name);

    List<TestEnvironment> findByStatus(String status);
}
