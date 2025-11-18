package com.synapsetest.testmanagement.repository;

import com.synapsetest.testmanagement.model.QualityReport;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * QualityReport Repository (MongoDB)
 */
@Repository
// @Profile("mongodb") // Temporarily disabled to show in Swagger UI
public interface QualityReportRepository extends MongoRepository<QualityReport, String> {

    Optional<QualityReport> findByTaskId(String taskId);

    List<QualityReport> findByStatus(String status);

    List<QualityReport> findByGeneratedAtBetween(LocalDateTime start, LocalDateTime end);

    List<QualityReport> findTop10ByOrderByGeneratedAtDesc();
}
