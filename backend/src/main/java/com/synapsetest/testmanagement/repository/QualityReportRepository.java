package com.synapsetest.testmanagement.repository;

import com.synapsetest.testmanagement.model.QualityReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * QualityReport Repository (MongoDB)
 */
@Repository
public interface QualityReportRepository extends MongoRepository<QualityReport, String> {

    Optional<QualityReport> findByTaskId(String taskId);

    List<QualityReport> findByStatus(String status);

    List<QualityReport> findByGeneratedAtBetween(LocalDateTime start, LocalDateTime end);

    List<QualityReport> findTop10ByOrderByGeneratedAtDesc();
}
