package com.synapsetest.testmanagement.service;

import com.synapsetest.testmanagement.exception.ResourceNotFoundException;
import com.synapsetest.testmanagement.exception.ValidationException;
import com.synapsetest.testmanagement.model.ResourcePool;
import com.synapsetest.testmanagement.repository.ResourcePoolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * ResourcePool Service
 * Business logic for resource pool management
 *
 * Task: T033 [US1] Implement ResourcePoolService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResourcePoolService {

    private final ResourcePoolRepository resourcePoolRepository;

    /**
     * Get all available resource pools
     */
    public List<ResourcePool> getAvailableResourcePools() {
        return resourcePoolRepository.findByStatus(ResourcePool.Status.AVAILABLE.name());
    }

    /**
     * Get resource pool by ID
     */
    public ResourcePool getResourcePoolById(UUID id) {
        return resourcePoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ResourcePool", "id", id));
    }

    /**
     * Get resource pools by type
     */
    public List<ResourcePool> getResourcePoolsByType(String type) {
        return resourcePoolRepository.findByType(type);
    }

    /**
     * Create a new resource pool
     */
    @Transactional
    public ResourcePool createResourcePool(ResourcePool resourcePool) {
        log.info("Creating resource pool: {}", resourcePool.getName());
        return resourcePoolRepository.save(resourcePool);
    }

    /**
     * Allocate resources from pool
     */
    @Transactional
    public ResourcePool allocateResources(UUID poolId, int count) {
        ResourcePool pool = getResourcePoolById(poolId);

        if (pool.getAvailableResources() < count) {
            throw new ValidationException(
                    String.format("Not enough resources in pool %s. Available: %d, Requested: %d",
                            pool.getName(), pool.getAvailableResources(), count));
        }

        pool.setUsed(pool.getUsed() + count);
        log.info("Allocated {} resources from pool {}", count, pool.getName());

        return resourcePoolRepository.save(pool);
    }

    /**
     * Release resources back to pool
     */
    @Transactional
    public ResourcePool releaseResources(UUID poolId, int count) {
        ResourcePool pool = getResourcePoolById(poolId);

        if (pool.getUsed() < count) {
            throw new ValidationException(
                    String.format("Cannot release %d resources from pool %s. Currently used: %d",
                            count, pool.getName(), pool.getUsed()));
        }

        pool.setUsed(pool.getUsed() - count);
        log.info("Released {} resources to pool {}", count, pool.getName());

        return resourcePoolRepository.save(pool);
    }

    /**
     * Find best available resource pool for a given type
     */
    public ResourcePool findBestAvailablePool(String type, int requiredCapacity) {
        List<ResourcePool> pools = resourcePoolRepository.findByStatusAndType(
                ResourcePool.Status.AVAILABLE.name(), type);

        return pools.stream()
                .filter(pool -> pool.getAvailableResources() >= requiredCapacity)
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        String.format("No available resource pool of type %s with capacity %d",
                                type, requiredCapacity)));
    }
}
