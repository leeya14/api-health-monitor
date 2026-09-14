package com.yoona.apihealthmonitor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record ExecutionCreateRequest(
        @NotNull Boolean success,
        @Min(100) @Max(599) Integer httpStatus,
        @NotNull @PositiveOrZero Long latencyMs,
        @Size(max = 1000) String errorMessage,
        LocalDateTime executedAt
) {}
