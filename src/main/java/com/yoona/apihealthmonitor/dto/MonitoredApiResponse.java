package com.yoona.apihealthmonitor.dto;

import java.time.LocalDateTime;

public record MonitoredApiResponse(
        Long id,
        String name,
        String endpointUrl,
        String httpMethod,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
