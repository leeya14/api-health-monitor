package com.yoona.apihealthmonitor.controller;

import com.yoona.apihealthmonitor.entity.MonitoredApi;
import com.yoona.apihealthmonitor.service.ApiMonitoringService;
import com.yoona.apihealthmonitor.service.MonitoredApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/monitored-apis")
public class MonitoringController {

    private final MonitoredApiService monitoredApiService;
    private final ApiMonitoringService monitoringService;

    public MonitoringController(
            MonitoredApiService monitoredApiService,
            ApiMonitoringService monitoringService
    ) {
        this.monitoredApiService = monitoredApiService;
        this.monitoringService = monitoringService;
    }

    @PostMapping("/{apiId}/check")
    public ResponseEntity<Map<String, Object>> checkNow(
            @PathVariable Long apiId
    ) {

        MonitoredApi api =
                monitoredApiService.getEntity(apiId);

        monitoringService.checkApi(api);

        return ResponseEntity.ok(
                Map.of(
                        "apiId", api.getId(),
                        "name", api.getName(),
                        "message", "API monitoring completed"
                )
        );
    }
}