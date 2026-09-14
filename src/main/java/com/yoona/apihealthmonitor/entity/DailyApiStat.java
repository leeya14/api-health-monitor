package com.yoona.apihealthmonitor.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_api_stat",
       uniqueConstraints = @UniqueConstraint(name = "uk_daily_stat_api_date", columnNames = {"monitored_api_id", "stat_date"}))
public class DailyApiStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "monitored_api_id", nullable = false)
    private MonitoredApi monitoredApi;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;

    @Column(nullable = false)
    private long totalCount;

    @Column(nullable = false)
    private long successCount;

    @Column(nullable = false)
    private long failureCount;

    @Column(nullable = false)
    private double successRate;

    @Column(nullable = false)
    private double averageLatencyMs;

    @Column(nullable = false)
    private LocalDateTime calculatedAt;

    @PrePersist
    @PreUpdate
    void touchCalculatedAt() {
        calculatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public MonitoredApi getMonitoredApi() { return monitoredApi; }
    public void setMonitoredApi(MonitoredApi monitoredApi) { this.monitoredApi = monitoredApi; }
    public LocalDate getStatDate() { return statDate; }
    public void setStatDate(LocalDate statDate) { this.statDate = statDate; }
    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
    public long getSuccessCount() { return successCount; }
    public void setSuccessCount(long successCount) { this.successCount = successCount; }
    public long getFailureCount() { return failureCount; }
    public void setFailureCount(long failureCount) { this.failureCount = failureCount; }
    public double getSuccessRate() { return successRate; }
    public void setSuccessRate(double successRate) { this.successRate = successRate; }
    public double getAverageLatencyMs() { return averageLatencyMs; }
    public void setAverageLatencyMs(double averageLatencyMs) { this.averageLatencyMs = averageLatencyMs; }
    public LocalDateTime getCalculatedAt() { return calculatedAt; }
}
