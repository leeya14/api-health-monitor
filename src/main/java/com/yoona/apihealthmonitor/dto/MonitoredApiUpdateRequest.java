package com.yoona.apihealthmonitor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MonitoredApiUpdateRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 500) String endpointUrl,
        @NotBlank @Pattern(regexp = "GET|POST|PUT|PATCH|DELETE", message = "지원하지 않는 HTTP Method입니다.") String httpMethod,
        boolean active
) {}
