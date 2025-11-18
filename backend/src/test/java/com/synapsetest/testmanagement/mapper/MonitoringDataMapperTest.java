package com.synapsetest.testmanagement.mapper;

import com.synapsetest.testmanagement.model.MonitoringData;
import com.synapsetest.testmanagement.repository.MonitoringDataRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository层单元测试 - MonitoringData (MongoDB)
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
 *
 * Note: This test uses MongoDB for time-series data storage
 */
@DataMongoTest
@ActiveProfiles("mongodb")
@DisplayName("MonitoringData Repository测试")
public class MonitoringDataMapperTest {

    @Autowired
    private MonitoringDataRepository monitoringDataRepository;

    @BeforeEach
    void setUp() {
        // 清理测试数据
        monitoringDataRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        monitoringDataRepository.deleteAll();
    }

    @Test
    @DisplayName("Repository测试1: 插入监控数据")
    void insert_WithValidMonitoringData_ShouldPersist() {
        // Given: 准备监控数据
        MonitoringData data = new MonitoringData();
        data.setTaskId("task-001");
        data.setStatus("RUNNING");
        data.setProgress(50);
        data.setExecutedCases(50);
        data.setTotalCases(100);
        data.setPassedCases(45);
        data.setFailedCases(5);
        data.setSkippedCases(0);
        data.setStartTime(LocalDateTime.now());
        data.setTimestamp(LocalDateTime.now());
        data.setEnvironment("DEV");
        data.setVersion("v1.0.0");

        // When: 执行插入
        MonitoringData saved = monitoringDataRepository.save(data);

        // Then: 验证插入成功
        assertNotNull(saved.getId());
        assertEquals("task-001", saved.getTaskId());
        assertEquals("RUNNING", saved.getStatus());
        assertEquals(50, saved.getProgress());

        // 验证可以查询到
        Optional<MonitoringData> found = monitoringDataRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("task-001", found.get().getTaskId());
    }

    @Test
    @DisplayName("Repository测试2: 根据任务ID查询监控数据")
    void findByTaskId_WithValidTaskId_ShouldReturnData() {
        // Given: 插入测试数据
        MonitoringData data = createMonitoringData("task-001", "RUNNING", 50);
        monitoringDataRepository.save(data);

        // When: 根据任务ID查询
        Optional<MonitoringData> found = monitoringDataRepository.findByTaskId("task-001");

        // Then: 验证查询结果
        assertTrue(found.isPresent());
        assertEquals("task-001", found.get().getTaskId());
        assertEquals("RUNNING", found.get().getStatus());
    }

    @Test
    @DisplayName("Repository测试3: 根据状态查询监控数据")
    void findByStatus_WithValidStatus_ShouldReturnList() {
        // Given: 插入多条不同状态的数据
        monitoringDataRepository.save(createMonitoringData("task-001", "RUNNING", 50));
        monitoringDataRepository.save(createMonitoringData("task-002", "RUNNING", 60));
        monitoringDataRepository.save(createMonitoringData("task-003", "COMPLETED", 100));

        // When: 根据状态查询
        List<MonitoringData> runningTasks = monitoringDataRepository.findByStatus("RUNNING");

        // Then: 验证查询结果
        assertNotNull(runningTasks);
        assertEquals(2, runningTasks.size());
        runningTasks.forEach(task -> assertEquals("RUNNING", task.getStatus()));
    }

    @Test
    @DisplayName("Repository测试4: 根据时间范围查询监控数据")
    void findByTimestampBetween_WithValidRange_ShouldReturnFilteredData() {
        // Given: 插入不同时间的数据
        LocalDateTime now = LocalDateTime.now();
        
        MonitoringData data1 = createMonitoringData("task-001", "RUNNING", 50);
        data1.setTimestamp(now.minusHours(2));
        monitoringDataRepository.save(data1);

        MonitoringData data2 = createMonitoringData("task-002", "RUNNING", 60);
        data2.setTimestamp(now.minusHours(1));
        monitoringDataRepository.save(data2);

        MonitoringData data3 = createMonitoringData("task-003", "COMPLETED", 100);
        data3.setTimestamp(now.minusHours(5));
        monitoringDataRepository.save(data3);

        // When: 查询最近3小时的数据
        LocalDateTime startTime = now.minusHours(3);
        LocalDateTime endTime = now;
        List<MonitoringData> recentData = monitoringDataRepository.findByTimestampBetween(startTime, endTime);

        // Then: 验证查询结果
        assertNotNull(recentData);
        assertEquals(2, recentData.size());
        recentData.forEach(data -> {
            assertTrue(data.getTimestamp().isAfter(startTime) || data.getTimestamp().isEqual(startTime));
            assertTrue(data.getTimestamp().isBefore(endTime) || data.getTimestamp().isEqual(endTime));
        });
    }

    @Test
    @DisplayName("Repository测试5: 根据环境查询监控数据")
    void findByEnvironment_WithValidEnvironment_ShouldReturnList() {
        // Given: 插入不同环境的数据
        MonitoringData devData = createMonitoringData("task-001", "RUNNING", 50);
        devData.setEnvironment("DEV");
        monitoringDataRepository.save(devData);

        MonitoringData testData = createMonitoringData("task-002", "RUNNING", 60);
        testData.setEnvironment("TEST");
        monitoringDataRepository.save(testData);

        // When: 查询DEV环境的数据
        List<MonitoringData> devMonitoring = monitoringDataRepository.findByEnvironment("DEV");

        // Then: 验证查询结果
        assertNotNull(devMonitoring);
        assertEquals(1, devMonitoring.size());
        assertEquals("DEV", devMonitoring.get(0).getEnvironment());
    }

    @Test
    @DisplayName("Repository测试6: 查询最近20条监控数据")
    void findTop20ByOrderByTimestampDesc_ShouldReturnRecentData() {
        // Given: 插入30条数据
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 30; i++) {
            MonitoringData data = createMonitoringData("task-" + i, "RUNNING", 50);
            data.setTimestamp(now.minusMinutes(i));
            monitoringDataRepository.save(data);
        }

        // When: 查询最近20条
        List<MonitoringData> recentData = monitoringDataRepository.findTop20ByOrderByTimestampDesc();

        // Then: 验证查询结果
        assertNotNull(recentData);
        assertTrue(recentData.size() <= 20);
        
        // 验证按时间倒序排列
        for (int i = 0; i < recentData.size() - 1; i++) {
            assertTrue(recentData.get(i).getTimestamp().isAfter(recentData.get(i + 1).getTimestamp()) ||
                      recentData.get(i).getTimestamp().isEqual(recentData.get(i + 1).getTimestamp()));
        }
    }

    @Test
    @DisplayName("Repository测试7: 更新监控数据")
    void update_WithValidData_ShouldUpdateSuccessfully() {
        // Given: 插入初始数据
        MonitoringData data = createMonitoringData("task-001", "RUNNING", 50);
        MonitoringData saved = monitoringDataRepository.save(data);

        // When: 更新进度
        saved.setProgress(80);
        saved.setExecutedCases(80);
        saved.setPassedCases(75);
        MonitoringData updated = monitoringDataRepository.save(saved);

        // Then: 验证更新成功
        assertEquals(80, updated.getProgress());
        assertEquals(80, updated.getExecutedCases());
        assertEquals(75, updated.getPassedCases());
    }

    @Test
    @DisplayName("Repository测试8: 删除监控数据")
    void delete_WithValidId_ShouldDeleteSuccessfully() {
        // Given: 插入测试数据
        MonitoringData data = createMonitoringData("task-001", "COMPLETED", 100);
        MonitoringData saved = monitoringDataRepository.save(data);
        String id = saved.getId();

        // When: 删除数据
        monitoringDataRepository.deleteById(id);

        // Then: 验证删除成功
        Optional<MonitoringData> deleted = monitoringDataRepository.findById(id);
        assertFalse(deleted.isPresent());
    }

    @Test
    @DisplayName("Repository测试9: 测试通过率计算")
    void calculatePassRate_ShouldReturnCorrectPercentage() {
        // Given: 创建包含用例执行数据的监控数据
        MonitoringData data = new MonitoringData();
        data.setTaskId("task-001");
        data.setExecutedCases(100);
        data.setPassedCases(85);
        data.setFailedCases(15);

        // When: 计算通过率
        Double passRate = data.getPassRate();

        // Then: 验证计算结果
        assertNotNull(passRate);
        assertEquals(85.0, passRate, 0.01);
    }

    @Test
    @DisplayName("Repository测试10: 存储复杂的资源使用和性能指标数据")
    void save_WithComplexMetrics_ShouldPersistCorrectly() {
        // Given: 准备包含复杂指标的监控数据
        MonitoringData data = createMonitoringData("task-001", "RUNNING", 50);
        
        Map<String, Object> resourceUsage = new HashMap<>();
        resourceUsage.put("cpu_usage", 75.5);
        resourceUsage.put("memory_mb", 2048);
        resourceUsage.put("disk_io", "120MB/s");
        data.setResourceUsage(resourceUsage);

        Map<String, Object> performanceMetrics = new HashMap<>();
        performanceMetrics.put("avg_response_time_ms", 150);
        performanceMetrics.put("throughput_rps", 1000);
        performanceMetrics.put("error_rate", 0.05);
        data.setPerformanceMetrics(performanceMetrics);

        // When: 保存数据
        MonitoringData saved = monitoringDataRepository.save(data);

        // Then: 验证复杂字段正确保存
        assertNotNull(saved.getId());
        assertNotNull(saved.getResourceUsage());
        assertEquals(75.5, saved.getResourceUsage().get("cpu_usage"));
        assertNotNull(saved.getPerformanceMetrics());
        assertEquals(150, saved.getPerformanceMetrics().get("avg_response_time_ms"));
    }

    /**
     * 辅助方法：创建监控数据
     */
    private MonitoringData createMonitoringData(String taskId, String status, int progress) {
        MonitoringData data = new MonitoringData();
        data.setTaskId(taskId);
        data.setStatus(status);
        data.setProgress(progress);
        data.setExecutedCases(progress);
        data.setTotalCases(100);
        data.setPassedCases((int) (progress * 0.9));
        data.setFailedCases((int) (progress * 0.1));
        data.setSkippedCases(0);
        data.setStartTime(LocalDateTime.now());
        data.setTimestamp(LocalDateTime.now());
        data.setEnvironment("DEV");
        data.setVersion("v1.0.0");
        return data;
    }
}
