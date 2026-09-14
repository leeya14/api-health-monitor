package com.yoona.apihealthmonitor.repository;

import com.yoona.apihealthmonitor.entity.ApiExecutionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ApiExecutionLogRepository extends JpaRepository<ApiExecutionLog, Long> {

    Page<ApiExecutionLog> findByMonitoredApiIdOrderByExecutedAtDesc(
            Long monitoredApiId,
            Pageable pageable
    );

    Page<ApiExecutionLog> findByMonitoredApiIdAndSuccessOrderByExecutedAtDesc(
            Long monitoredApiId,
            boolean success,
            Pageable pageable
    );

    @Query("""
        select count(l) as totalCount,
               coalesce(sum(case when l.success = true then 1 else 0 end), 0) as successCount,
               coalesce(sum(case when l.success = false then 1 else 0 end), 0) as failureCount,
               coalesce(avg(l.latencyMs), 0.0) as averageLatencyMs
        from ApiExecutionLog l
        where l.monitoredApi.id = :apiId
          and l.executedAt >= :start
          and l.executedAt < :end
        """)
    ExecutionAggregation aggregate(@Param("apiId") Long apiId,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);
}