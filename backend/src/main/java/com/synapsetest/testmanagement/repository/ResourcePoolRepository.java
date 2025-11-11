package com.synapsetest.testmanagement.repository;

import com.synapsetest.testmanagement.model.ResourcePool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ResourcePool Repository
 */
@Repository
public interface ResourcePoolRepository extends JpaRepository<ResourcePool, UUID> {

    Optional<ResourcePool> findByName(String name);

    List<ResourcePool> findByStatus(String status);

    List<ResourcePool> findByType(String type);

    List<ResourcePool> findByStatusAndType(String status, String type);
}
