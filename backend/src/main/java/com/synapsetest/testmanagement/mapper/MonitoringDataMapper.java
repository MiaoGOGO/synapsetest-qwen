package com.synapsetest.testmanagement.mapper;

import com.synapsetest.testmanagement.model.MonitoringData;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MonitoringData MyBatis Mapper
 * SQL statements are defined in MonitoringDataMapper.xml
 */
@Mapper
public interface MonitoringDataMapper {

    MonitoringData selectById(String id);

    List<MonitoringData> selectAll();

    List<MonitoringData> selectByTaskId(String taskId);

    List<MonitoringData> selectByStatus(String status);

    List<MonitoringData> selectByEnvironment(String environment);

    List<MonitoringData> selectByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);

    MonitoringData selectLatestByTaskId(String taskId);

    int insert(MonitoringData monitoringData);

    int update(MonitoringData monitoringData);

    int deleteById(String id);

    int deleteByTaskId(String taskId);

    int count();
}

