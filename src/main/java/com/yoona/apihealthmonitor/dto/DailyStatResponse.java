package com.yoona.apihealthmonitor.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DailyStatResponse(
        Long monitoredApiId,
        String apiName,
        LocalDate statDate,
        long totalCount,
        long successCount,
        long failureCount,
        double successRate,
        double averageLatencyMs,
        LocalDateTime calculatedAt
) {}
