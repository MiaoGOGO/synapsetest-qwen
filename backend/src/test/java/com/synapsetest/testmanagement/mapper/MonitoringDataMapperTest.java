package com.synapsetest.testmanagement.mapper;

import com.synapsetest.testmanagement.entity.MonitoringData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Mapper层单元测试 - MonitoringDataMapper
 *
 * 测试目标：
 * 1. 验证监控数据的写入和查询
 * 2. 验证时间范围查询的准确性
 * 3. 验证聚合统计的正确性
 * 4. 验证实时数据和历史数据的分离查询
 *
 * 重点测试场景：
 * - 时间序列数据的存储
 * - 按时间范围的高效查询
 * - 统计聚合（成功率、平均执行时间等）
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("MonitoringDataMapper数据访问层测试")
public class MonitoringDataMapperTest {

    @Autowired
    private MonitoringDataMapper monitoringDataMapper;

    @Test
    @DisplayName("Mapper测试1: 插入监控数据")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void insert_WithValidMonitoringData_ShouldPersist() {
        // Given: 准备监控数据
        MonitoringData data = new MonitoringData();
        data.setTaskId(1L);
        data.setMetricType("EXECUTION_TIME");
        data.setMetricValue(125.5);
        data.setUnit("seconds");
        data.setRecordedAt(LocalDateTime.now());
        data.setCreatedAt(LocalDateTime.now());

        // When: 执行插入
        int affectedRows = monitoringDataMapper.insert(data);

        // Then: 验证插入成功
        assertEquals(1, affectedRows);
        assertNotNull(data.getId());

        MonitoringData inserted = monitoringDataMapper.selectById(data.getId());
        assertNotNull(inserted);
        assertEquals(125.5, inserted.getMetricValue());
    }

    @Test
    @DisplayName("Mapper测试2: 根据任务ID查询监控数据")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void selectByTaskId_WithValidTaskId_ShouldReturnList() {
        // Given: 任务ID
        Long taskId = 1L;

        // When: 根据任务ID查询
        List<MonitoringData> dataList = monitoringDataMapper.selectByTaskId(taskId);

        // Then: 验证查询结果
        assertNotNull(dataList);
        assertTrue(dataList.size() > 0);
        dataList.forEach(data -> assertEquals(taskId, data.getTaskId()));
    }

    @Test
    @DisplayName("Mapper测试3: 根据时间范围查询监控数据")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void selectByTimeRange_WithValidRange_ShouldReturnFilteredData() {
        // Given: 时间范围
        LocalDateTime startTime = LocalDateTime.now().minusHours(24);
        LocalDateTime endTime = LocalDateTime.now();

        // When: 根据时间范围查询
        List<MonitoringData> dataList = monitoringDataMapper.selectByTimeRange(startTime, endTime);

        // Then: 验证查询结果
        assertNotNull(dataList);
        dataList.forEach(data -> {
            assertTrue(data.getRecordedAt().isAfter(startTime) || data.getRecordedAt().isEqual(startTime));
            assertTrue(data.getRecordedAt().isBefore(endTime) || data.getRecordedAt().isEqual(endTime));
        });
    }

    @Test
    @DisplayName("Mapper测试4: 根据指标类型查询数据")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void selectByMetricType_WithValidType_ShouldReturnList() {
        // Given: 指标类型
        String metricType = "PASS_RATE";

        // When: 根据指标类型查询
        List<MonitoringData> dataList = monitoringDataMapper.selectByMetricType(metricType);

        // Then: 验证查询结果
        assertNotNull(dataList);
        assertTrue(dataList.size() > 0);
        dataList.forEach(data -> assertEquals(metricType, data.getMetricType()));
    }

    @Test
    @DisplayName("Mapper测试5: 统计任务的平均执行时间")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void calculateAverageExecutionTime_ShouldReturnCorrectValue() {
        // Given: 任务ID和指标类型
        Long taskId = 1L;
        String metricType = "EXECUTION_TIME";

        // When: 计算平均值
        Double avgTime = monitoringDataMapper.calculateAverage(taskId, metricType);

        // Then: 验证统计结果
        assertNotNull(avgTime);
        assertTrue(avgTime > 0);
    }

    @Test
    @DisplayName("Mapper测试6: 统计任务的测试通过率")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void calculatePassRate_ShouldReturnCorrectPercentage() {
        // Given: 任务ID
        Long taskId = 1L;

        // When: 查询通过率数据
        List<MonitoringData> passRateData = monitoringDataMapper.selectByTaskIdAndMetricType(taskId, "PASS_RATE");

        // Then: 验证查询结果
        assertNotNull(passRateData);
        if (!passRateData.isEmpty()) {
            passRateData.forEach(data -> {
                assertTrue(data.getMetricValue() >= 0 && data.getMetricValue() <= 100);
            });
        }
    }

    @Test
    @DisplayName("Mapper测试7: 批量插入监控数据")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void batchInsert_WithMultipleRecords_ShouldInsertAll() {
        // Given: 准备批量监控数据
        List<MonitoringData> dataList = List.of(
            createMonitoringData(1L, "CPU_USAGE", 45.2),
            createMonitoringData(1L, "MEMORY_USAGE", 68.5),
            createMonitoringData(1L, "DISK_USAGE", 32.1)
        );

        // When: 批量插入
        int affectedRows = monitoringDataMapper.batchInsert(dataList);

        // Then: 验证插入成功
        assertEquals(3, affectedRows);
        dataList.forEach(data -> assertNotNull(data.getId()));
    }

    @Test
    @DisplayName("Mapper测试8: 查询最近N条监控数据")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void selectRecentData_WithLimit_ShouldReturnLimitedRecords() {
        // Given: 任务ID和查询数量
        Long taskId = 1L;
        int limit = 10;

        // When: 查询最近的监控数据
        List<MonitoringData> dataList = monitoringDataMapper.selectRecentByTaskId(taskId, limit);

        // Then: 验证查询结果
        assertNotNull(dataList);
        assertTrue(dataList.size() <= limit);

        // 验证数据按时间倒序排列
        if (dataList.size() > 1) {
            for (int i = 0; i < dataList.size() - 1; i++) {
                assertTrue(dataList.get(i).getRecordedAt().isAfter(dataList.get(i + 1).getRecordedAt()) ||
                          dataList.get(i).getRecordedAt().isEqual(dataList.get(i + 1).getRecordedAt()));
            }
        }
    }

    @Test
    @DisplayName("Mapper测试9: 删除过期监控数据")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteOldData_WithCutoffDate_ShouldRemoveExpiredRecords() {
        // Given: 删除30天前的数据
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);

        // When: 删除过期数据
        int deletedCount = monitoringDataMapper.deleteBeforeDate(cutoffDate);

        // Then: 验证删除操作
        assertTrue(deletedCount >= 0);

        // 验证剩余数据都在截止日期之后
        List<MonitoringData> remainingData = monitoringDataMapper.selectByTimeRange(
            cutoffDate.minusDays(365),
            LocalDateTime.now()
        );
        remainingData.forEach(data ->
            assertTrue(data.getRecordedAt().isAfter(cutoffDate) || data.getRecordedAt().isEqual(cutoffDate))
        );
    }

    @Test
    @DisplayName("Mapper测试10: 聚合统计 - 按指标类型分组")
    @Sql(scripts = "/test-data-mapper.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void aggregateByMetricType_ShouldReturnGroupedStats() {
        // Given: 任务ID
        Long taskId = 1L;

        // When: 按指标类型聚合统计
        List<Map<String, Object>> stats = monitoringDataMapper.aggregateByMetricType(taskId);

        // Then: 验证统计结果
        assertNotNull(stats);
        stats.forEach(stat -> {
            assertNotNull(stat.get("metric_type"));
            assertNotNull(stat.get("avg_value"));
            assertNotNull(stat.get("max_value"));
            assertNotNull(stat.get("min_value"));
            assertNotNull(stat.get("count"));
        });
    }

    /**
     * 辅助方法：创建监控数据
     */
    private MonitoringData createMonitoringData(Long taskId, String metricType, Double metricValue) {
        MonitoringData data = new MonitoringData();
        data.setTaskId(taskId);
        data.setMetricType(metricType);
        data.setMetricValue(metricValue);
        data.setUnit("percent");
        data.setRecordedAt(LocalDateTime.now());
        data.setCreatedAt(LocalDateTime.now());
        return data;
    }
}
