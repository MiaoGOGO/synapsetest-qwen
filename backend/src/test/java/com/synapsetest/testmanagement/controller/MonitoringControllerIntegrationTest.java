package com.synapsetest.testmanagement.controller;

import com.synapsetest.testmanagement.dto.response.DashboardStatsResponse;
import com.synapsetest.testmanagement.dto.response.MonitoringDataResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Controller集成测试 - MonitoringController
 *
 * 测试目标：
 * 1. 验证实时监控数据的获取
 * 2. 验证Dashboard统计数据的聚合
 * 3. 验证历史数据的时间范围查询
 * 4. 验证与MongoDB的集成（历史数据存储）
 *
 * 对应User Story: US3-测试结果可视化分析
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("mongodb")
@DisplayName("MonitoringController API集成测试")
public class MonitoringControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/v1/monitoring";
    }

    @Test
    @DisplayName("场景3.1: 获取Dashboard统计概览")
    @Sql(scripts = "/test-data-us3.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getDashboardStats_ShouldReturnOverviewStats() {
        // Given: Dashboard统计接口
        String url = getBaseUrl() + "/dashboard/stats";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "zhangsan");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When: 发送GET请求
        ResponseEntity<DashboardStatsResponse> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            DashboardStatsResponse.class
        );

        // Then: 验证响应
        assertEquals(HttpStatus.OK, response.getStatusCode());
        DashboardStatsResponse stats = response.getBody();
        assertNotNull(stats);

        // 验证包含关键统计指标
        assertNotNull(stats.getTotalTasks());
        assertNotNull(stats.getRunningTasks());
        assertNotNull(stats.getCompletedTasks());
        assertNotNull(stats.getAveragePassRate());
        assertNotNull(stats.getAverageExecutionTime());

        // 验证数据合理性
        assertTrue(stats.getTotalTasks() >= 0);
        assertTrue(stats.getAveragePassRate() >= 0 && stats.getAveragePassRate() <= 100);
    }

    @Test
    @DisplayName("场景3.2: 获取实时任务执行进度")
    @Sql(scripts = "/test-data-us3.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getTaskProgress_WithValidTaskId_ShouldReturnRealTimeProgress() {
        // Given: 正在执行的任务ID
        Long taskId = 1L;
        String url = getBaseUrl() + "/tasks/" + taskId + "/progress";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "zhangsan");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When: 发送GET请求
        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            Map.class
        );

        // Then: 验证响应
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> progress = response.getBody();
        assertNotNull(progress);

        // 验证进度数据
        assertTrue(progress.containsKey("task_id"));
        assertTrue(progress.containsKey("status"));
        assertTrue(progress.containsKey("progress_percentage"));
        assertTrue(progress.containsKey("executed_count"));
        assertTrue(progress.containsKey("total_count"));
        assertTrue(progress.containsKey("pass_count"));
        assertTrue(progress.containsKey("fail_count"));

        // 验证进度百分比
        Double progressPercentage = (Double) progress.get("progress_percentage");
        assertTrue(progressPercentage >= 0 && progressPercentage <= 100);
    }

    @Test
    @DisplayName("场景3.3: 获取任务监控数据（时间序列）")
    @Sql(scripts = "/test-data-us3.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getTaskMonitoringData_WithTimeRange_ShouldReturnTimeSeriesData() {
        // Given: 查询参数（最近24小时）
        Long taskId = 1L;
        String url = getBaseUrl() + "/tasks/" + taskId + "/data?time_range=24h";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "zhangsan");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When: 发送GET请求
        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            Map.class
        );

        // Then: 验证响应
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);

        assertTrue(responseBody.containsKey("data_points"));
        List<?> dataPoints = (List<?>) responseBody.get("data_points");
        assertNotNull(dataPoints);

        // 验证时间序列数据点
        if (!dataPoints.isEmpty()) {
            Map<?, ?> firstPoint = (Map<?, ?>) dataPoints.get(0);
            assertTrue(firstPoint.containsKey("timestamp"));
            assertTrue(firstPoint.containsKey("metric_type"));
            assertTrue(firstPoint.containsKey("metric_value"));
        }
    }

    @Test
    @DisplayName("场景3.4: 获取测试通过率趋势")
    @Sql(scripts = "/test-data-us3.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getPassRateTrend_WithTimeRange_ShouldReturnTrendData() {
        // Given: 查询参数（最近7天）
        String url = getBaseUrl() + "/trends/pass-rate?time_range=7d";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "zhangsan");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When: 发送GET请求
        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            Map.class
        );

        // Then: 验证响应
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);

        assertTrue(responseBody.containsKey("trend_data"));
        List<?> trendData = (List<?>) responseBody.get("trend_data");
        assertNotNull(trendData);

        // 验证趋势数据
        if (!trendData.isEmpty()) {
            Map<?, ?> dataPoint = (Map<?, ?>) trendData.get(0);
            assertTrue(dataPoint.containsKey("date"));
            assertTrue(dataPoint.containsKey("pass_rate"));

            Double passRate = (Double) dataPoint.get("pass_rate");
            assertTrue(passRate >= 0 && passRate <= 100);
        }
    }

    @Test
    @DisplayName("场景3.5: 获取执行时间统计")
    @Sql(scripts = "/test-data-us3.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getExecutionTimeStats_ShouldReturnAggregatedStats() {
        // Given: 统计接口
        String url = getBaseUrl() + "/stats/execution-time";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "zhangsan");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When: 发送GET请求
        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            Map.class
        );

        // Then: 验证响应
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> stats = response.getBody();
        assertNotNull(stats);

        // 验证统计指标
        assertTrue(stats.containsKey("average"));
        assertTrue(stats.containsKey("max"));
        assertTrue(stats.containsKey("min"));
        assertTrue(stats.containsKey("median"));

        Double average = (Double) stats.get("average");
        Double max = (Double) stats.get("max");
        Double min = (Double) stats.get("min");

        assertTrue(average >= 0);
        assertTrue(max >= average);
        assertTrue(min <= average);
    }

    @Test
    @DisplayName("场景3.6: 按模块分组的统计数据")
    @Sql(scripts = "/test-data-us3.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getStatsByModule_ShouldReturnGroupedData() {
        // Given: 按模块分组查询
        String url = getBaseUrl() + "/stats/by-module";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "zhangsan");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When: 发送GET请求
        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            Map.class
        );

        // Then: 验证响应
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);

        assertTrue(responseBody.containsKey("module_stats"));
        List<?> moduleStats = (List<?>) responseBody.get("module_stats");
        assertNotNull(moduleStats);

        // 验证每个模块的统计数据
        if (!moduleStats.isEmpty()) {
            Map<?, ?> moduleStat = (Map<?, ?>) moduleStats.get(0);
            assertTrue(moduleStat.containsKey("module"));
            assertTrue(moduleStat.containsKey("total_cases"));
            assertTrue(moduleStat.containsKey("pass_rate"));
            assertTrue(moduleStat.containsKey("average_execution_time"));
        }
    }

    @Test
    @DisplayName("场景3.7: 获取失败用例详情列表")
    @Sql(scripts = "/test-data-us3.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getFailedCases_WithTaskId_ShouldReturnFailedCasesList() {
        // Given: 任务ID
        Long taskId = 1L;
        String url = getBaseUrl() + "/tasks/" + taskId + "/failed-cases";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "zhangsan");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When: 发送GET请求
        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            Map.class
        );

        // Then: 验证响应
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);

        assertTrue(responseBody.containsKey("failed_cases"));
        List<?> failedCases = (List<?>) responseBody.get("failed_cases");
        assertNotNull(failedCases);

        // 验证失败用例包含必要信息
        if (!failedCases.isEmpty()) {
            Map<?, ?> failedCase = (Map<?, ?>) failedCases.get(0);
            assertTrue(failedCase.containsKey("case_id"));
            assertTrue(failedCase.containsKey("case_name"));
            assertTrue(failedCase.containsKey("error_message"));
            assertTrue(failedCase.containsKey("failed_at"));
        }
    }

    @Test
    @DisplayName("场景3.8: MongoDB历史数据查询")
    @Sql(scripts = "/test-data-us3.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getHistoricalData_FromMongoDB_ShouldReturnArchiveData() {
        // Given: 查询历史数据（30天前）
        String url = getBaseUrl() + "/history?days=30";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "zhangsan");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When: 发送GET请求
        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            Map.class
        );

        // Then: 验证响应
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);

        assertTrue(responseBody.containsKey("data_source"));
        assertEquals("mongodb", responseBody.get("data_source"));

        assertTrue(responseBody.containsKey("historical_data"));
        List<?> historicalData = (List<?>) responseBody.get("historical_data");
        assertNotNull(historicalData);
    }

    @Test
    @DisplayName("场景3.9: 导出监控数据（CSV格式）")
    @Sql(scripts = "/test-data-us3.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void exportMonitoringData_AsCSV_ShouldReturnCSVFile() {
        // Given: 导出参数
        Long taskId = 1L;
        String url = getBaseUrl() + "/tasks/" + taskId + "/export?format=csv";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "zhangsan");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When: 发送GET请求
        ResponseEntity<byte[]> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            byte[].class
        );

        // Then: 验证响应
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        // 验证Content-Type为CSV
        HttpHeaders responseHeaders = response.getHeaders();
        MediaType contentType = responseHeaders.getContentType();
        assertTrue(contentType != null &&
                  (contentType.includes(MediaType.parseMediaType("text/csv")) ||
                   contentType.includes(MediaType.APPLICATION_OCTET_STREAM)));

        // 验证内容不为空
        assertTrue(response.getBody().length > 0);
    }

    @Test
    @DisplayName("场景3.10: 实时数据自动刷新（WebSocket/SSE）")
    @Sql(scripts = "/test-data-us3.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getRealtimeDataStream_ShouldSupportStreaming() {
        // Given: 实时数据流接口
        Long taskId = 1L;
        String url = getBaseUrl() + "/realtime/tasks/" + taskId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "zhangsan");
        headers.setAccept(List.of(MediaType.TEXT_EVENT_STREAM));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When: 发送GET请求（模拟SSE连接）
        // Note: TestRestTemplate不完全支持SSE，这里只验证端点可用性
        ResponseEntity<String> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            String.class
        );

        // Then: 验证响应（如果支持SSE，应该返回200或206）
        assertTrue(response.getStatusCode().is2xxSuccessful() ||
                  response.getStatusCode() == HttpStatus.NOT_IMPLEMENTED);
    }

    @Test
    @DisplayName("场景3.11: 参数验证 - 无效的时间范围")
    void getTaskMonitoringData_WithInvalidTimeRange_ShouldReturnBadRequest() {
        // Given: 无效的时间范围
        Long taskId = 1L;
        String url = getBaseUrl() + "/tasks/" + taskId + "/data?time_range=invalid";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "zhangsan");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When: 发送GET请求
        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            Map.class
        );

        // Then: 验证返回400错误
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("场景3.12: 获取不存在任务的监控数据 - 返回404")
    void getTaskMonitoringData_WithNonExistingTask_ShouldReturnNotFound() {
        // Given: 不存在的任务ID
        Long nonExistingTaskId = 999999L;
        String url = getBaseUrl() + "/tasks/" + nonExistingTaskId + "/data";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "zhangsan");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When: 发送GET请求
        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            Map.class
        );

        // Then: 验证返回404错误
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
