package com.yoona.apihealthmonitor.service;

import com.yoona.apihealthmonitor.dto.ExecutionCreateRequest;
import com.yoona.apihealthmonitor.dto.MonitoredApiCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DailyStatServiceTest {
    @Autowired MonitoredApiService monitoredApiService;
    @Autowired ExecutionService executionService;
    @Autowired DailyStatService dailyStatService;

    @Test
    void 일별_성공률과_평균응답시간을_집계한다() {
        var api = monitoredApiService.create(
                new MonitoredApiCreateRequest("검색 API", "https://example.com/api/search", "GET", true));
        LocalDate date = LocalDate.of(2026, 9, 14);

        executionService.record(api.id(), new ExecutionCreateRequest(true, 200, 100L, null, date.atTime(10, 0)));
        executionService.record(api.id(), new ExecutionCreateRequest(true, 200, 200L, null, date.atTime(11, 0)));
        executionService.record(api.id(), new ExecutionCreateRequest(false, 500, 300L, "DB timeout", date.atTime(12, 0)));

        var stat = dailyStatService.aggregateApi(api.id(), date);

        assertThat(stat.totalCount()).isEqualTo(3);
        assertThat(stat.successCount()).isEqualTo(2);
        assertThat(stat.failureCount()).isEqualTo(1);
        assertThat(stat.successRate()).isEqualTo(66.67);
        assertThat(stat.averageLatencyMs()).isEqualTo(200.0);
    }
}
