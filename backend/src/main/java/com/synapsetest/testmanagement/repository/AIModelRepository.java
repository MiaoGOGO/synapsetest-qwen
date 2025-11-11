package com.synapsetest.testmanagement.repository;

import com.synapsetest.testmanagement.model.AIModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * AIModel Repository (MongoDB)
 */
@Repository
public interface AIModelRepository extends MongoRepository<AIModel, String> {

    Optional<AIModel> findByNameAndVersion(String name, String version);

    List<AIModel> findBySecurityStatus(String securityStatus);

    List<AIModel> findByComplianceStatus(String complianceStatus);

    List<AIModel> findByName(String name);
}
