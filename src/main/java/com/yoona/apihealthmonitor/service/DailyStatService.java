package com.yoona.apihealthmonitor.service;

import com.yoona.apihealthmonitor.dto.DailyStatResponse;
import com.yoona.apihealthmonitor.entity.DailyApiStat;
import com.yoona.apihealthmonitor.entity.MonitoredApi;
import com.yoona.apihealthmonitor.repository.ApiExecutionLogRepository;
import com.yoona.apihealthmonitor.repository.DailyApiStatRepository;
import com.yoona.apihealthmonitor.repository.ExecutionAggregation;
import com.yoona.apihealthmonitor.repository.MonitoredApiRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DailyStatService {
    private final MonitoredApiRepository apiRepository;
    private final ApiExecutionLogRepository executionRepository;
    private final DailyApiStatRepository statRepository;
    private final MonitoredApiService monitoredApiService;

    public DailyStatService(MonitoredApiRepository apiRepository,
                            ApiExecutionLogRepository executionRepository,
                            DailyApiStatRepository statRepository,
                            MonitoredApiService monitoredApiService) {
        this.apiRepository = apiRepository;
        this.executionRepository = executionRepository;
        this.statRepository = statRepository;
        this.monitoredApiService = monitoredApiService;
    }

    @Transactional
    public int aggregateDate(LocalDate date) {
        int count = 0;
        for (MonitoredApi api : apiRepository.findAll()) {
            aggregateOne(api, date);
            count++;
        }
        return count;
    }

    @Transactional
    public DailyStatResponse aggregateApi(Long apiId, LocalDate date) {
        MonitoredApi api = monitoredApiService.getEntity(apiId);
        return toResponse(aggregateOne(api, date));
    }

    public List<DailyStatResponse> findStats(Long apiId, LocalDate from, LocalDate to) {
        MonitoredApi api = monitoredApiService.getEntity(apiId);
        if (from.isAfter(to)) throw new IllegalArgumentException("from은 to보다 이후일 수 없습니다.");
        return statRepository.findByMonitoredApiIdAndStatDateBetweenOrderByStatDateDesc(apiId, from, to)
                .stream().map(stat -> toResponse(stat, api.getName())).toList();
    }

    private DailyApiStat aggregateOne(MonitoredApi api, LocalDate date) {
        ExecutionAggregation agg = executionRepository.aggregate(api.getId(), date.atStartOfDay(), date.plusDays(1).atStartOfDay());
        long total = value(agg.getTotalCount());
        long success = value(agg.getSuccessCount());
        long failure = value(agg.getFailureCount());
        double avgLatency = agg.getAverageLatencyMs() == null ? 0.0 : agg.getAverageLatencyMs();
        double successRate = total == 0 ? 0.0 : Math.round((success * 10000.0 / total)) / 100.0;

        DailyApiStat stat = statRepository.findByMonitoredApiIdAndStatDate(api.getId(), date)
                .orElseGet(DailyApiStat::new);
        stat.setMonitoredApi(api);
        stat.setStatDate(date);
        stat.setTotalCount(total);
        stat.setSuccessCount(success);
        stat.setFailureCount(failure);
        stat.setSuccessRate(successRate);
        stat.setAverageLatencyMs(Math.round(avgLatency * 100.0) / 100.0);
        return statRepository.save(stat);
    }

    private long value(Long value) { return value == null ? 0 : value; }

    private DailyStatResponse toResponse(DailyApiStat stat) {
        return toResponse(stat, stat.getMonitoredApi().getName());
    }

    private DailyStatResponse toResponse(DailyApiStat stat, String apiName) {
        return new DailyStatResponse(stat.getMonitoredApi().getId(), apiName, stat.getStatDate(), stat.getTotalCount(),
                stat.getSuccessCount(), stat.getFailureCount(), stat.getSuccessRate(), stat.getAverageLatencyMs(), stat.getCalculatedAt());
    }
}
