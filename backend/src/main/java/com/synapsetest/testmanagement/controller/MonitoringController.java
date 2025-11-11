package com.synapsetest.testmanagement.controller;

import com.synapsetest.testmanagement.dto.ApiResponse;
import com.synapsetest.testmanagement.model.MonitoringData;
import com.synapsetest.testmanagement.service.MonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Monitoring Controller
 * REST API endpoints for real-time monitoring
 *
 * Task: T066 [US3] Implement MonitoringController
 */
@RestController
@RequestMapping("/api/v1/monitoring")
@RequiredArgsConstructor
public class MonitoringController {

    private final MonitoringService monitoringService;

    /**
     * Get monitoring data by task ID
     * GET /api/v1/monitoring/tasks/{taskId}
     */
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<ApiResponse<MonitoringData>> getMonitoringData(@PathVariable String taskId) {
        MonitoringData data = monitoringService.getMonitoringDataByTaskId(taskId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * Get recent monitoring data
     * GET /api/v1/monitoring/recent
     */
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<MonitoringData>>> getRecentMonitoringData() {
        List<MonitoringData> data = monitoringService.getRecentMonitoringData();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * Get running tasks
     * GET /api/v1/monitoring/running
     */
    @GetMapping("/running")
    public ResponseEntity<ApiResponse<List<MonitoringData>>> getRunningTasks() {
        List<MonitoringData> data = monitoringService.getRunningTasks();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * Get dashboard statistics
     * GET /api/v1/monitoring/dashboard/stats
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStatistics() {
        Map<String, Object> stats = monitoringService.getDashboardStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * Get monitoring data by environment
     * GET /api/v1/monitoring/environment/{environment}
     */
    @GetMapping("/environment/{environment}")
    public ResponseEntity<ApiResponse<List<MonitoringData>>> getMonitoringDataByEnvironment(
            @PathVariable String environment) {
        List<MonitoringData> data = monitoringService.getMonitoringDataByEnvironment(environment);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * Update resource usage
     * POST /api/v1/monitoring/tasks/{taskId}/resource-usage
     */
    @PostMapping("/tasks/{taskId}/resource-usage")
    public ResponseEntity<ApiResponse<MonitoringData>> updateResourceUsage(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> resourceUsage) {
        MonitoringData data = monitoringService.updateResourceUsage(taskId, resourceUsage);
        return ResponseEntity.ok(ApiResponse.success("Resource usage updated", data));
    }

    /**
     * Update performance metrics
     * POST /api/v1/monitoring/tasks/{taskId}/performance-metrics
     */
    @PostMapping("/tasks/{taskId}/performance-metrics")
    public ResponseEntity<ApiResponse<MonitoringData>> updatePerformanceMetrics(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> performanceMetrics) {
        MonitoringData data = monitoringService.updatePerformanceMetrics(taskId, performanceMetrics);
        return ResponseEntity.ok(ApiResponse.success("Performance metrics updated", data));
    }
}
