package com.synapsetest.testmanagement.mapper;

import com.synapsetest.testmanagement.model.QualityReport;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * QualityReport MyBatis Mapper
 * SQL statements are defined in QualityReportMapper.xml
 */
@Mapper
public interface QualityReportMapper {

    QualityReport selectById(String id);

    List<QualityReport> selectAll();

    List<QualityReport> selectByTaskId(String taskId);

    List<QualityReport> selectByStatus(String status);

    List<QualityReport> selectByGeneratedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    List<QualityReport> selectTop10ByOrderByGeneratedAtDesc();

    int insert(QualityReport qualityReport);

    int update(QualityReport qualityReport);

    int deleteById(String id);

    int deleteByTaskId(String taskId);

    int count();
}

