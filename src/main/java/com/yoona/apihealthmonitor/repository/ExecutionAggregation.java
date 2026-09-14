package com.yoona.apihealthmonitor.repository;

public interface ExecutionAggregation {
    Long getTotalCount();
    Long getSuccessCount();
    Long getFailureCount();
    Double getAverageLatencyMs();
}
