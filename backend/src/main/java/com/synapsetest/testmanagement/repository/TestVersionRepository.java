package com.synapsetest.testmanagement.repository;

import com.synapsetest.testmanagement.model.TestVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * TestVersion Repository
 */
@Repository
public interface TestVersionRepository extends JpaRepository<TestVersion, UUID> {

    Optional<TestVersion> findByName(String name);

    List<TestVersion> findByProductVersion(String productVersion);

    Optional<TestVersion> findByProductVersionAndName(String productVersion, String name);
}
