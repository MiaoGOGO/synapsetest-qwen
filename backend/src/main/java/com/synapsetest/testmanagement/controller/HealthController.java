package com.synapsetest.testmanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.synapsetest.testmanagement.dto.ApiResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller
 * Provides health status endpoints
 */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/health")
    public ApiResponse<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "test-management-backend");
        return ApiResponse.success(status);
    }
}
