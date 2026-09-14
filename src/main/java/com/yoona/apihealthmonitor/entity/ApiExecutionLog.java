package com.yoona.apihealthmonitor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_execution_log",
       indexes = {
           @Index(name = "idx_execution_api_time", columnList = "monitored_api_id, executed_at"),
           @Index(name = "idx_execution_success", columnList = "success")
       })
public class ApiExecutionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "monitored_api_id", nullable = false)
    private MonitoredApi monitoredApi;

    @Column(nullable = false)
    private boolean success;

    private Integer httpStatus;

    @Column(nullable = false)
    private Long latencyMs;

    @Column(length = 1000)
    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime executedAt;

    @PrePersist
    void prePersist() {
        if (executedAt == null) executedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public MonitoredApi getMonitoredApi() { return monitoredApi; }
    public void setMonitoredApi(MonitoredApi monitoredApi) { this.monitoredApi = monitoredApi; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public Integer getHttpStatus() { return httpStatus; }
    public void setHttpStatus(Integer httpStatus) { this.httpStatus = httpStatus; }
    public Long getLatencyMs() { return latencyMs; }
    public void setLatencyMs(Long latencyMs) { this.latencyMs = latencyMs; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }
}
