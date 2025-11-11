package com.synapsetest.testmanagement.repository;

import com.synapsetest.testmanagement.model.MonitoringData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * MonitoringData Repository (MongoDB)
 */
@Repository
public interface MonitoringDataRepository extends MongoRepository<MonitoringData, String> {

    Optional<MonitoringData> findByTaskId(String taskId);

    List<MonitoringData> findByStatus(String status);

    List<MonitoringData> findByEnvironment(String environment);

    List<MonitoringData> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<MonitoringData> findTop20ByOrderByTimestampDesc();
}
