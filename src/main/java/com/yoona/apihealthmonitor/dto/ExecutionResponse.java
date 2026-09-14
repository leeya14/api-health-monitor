package com.yoona.apihealthmonitor.dto;

import java.time.LocalDateTime;

public record ExecutionResponse(
        Long id,
        Long monitoredApiId,
        boolean success,
        Integer httpStatus,
        Long latencyMs,
        String errorMessage,
        LocalDateTime executedAt
) {}
