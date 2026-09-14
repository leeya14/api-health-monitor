package com.yoona.apihealthmonitor.scheduler;

import com.yoona.apihealthmonitor.entity.MonitoredApi;
import com.yoona.apihealthmonitor.repository.MonitoredApiRepository;
import com.yoona.apihealthmonitor.service.ApiMonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApiMonitoringScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(ApiMonitoringScheduler.class);

    private final MonitoredApiRepository repository;
    private final ApiMonitoringService monitoringService;

    public ApiMonitoringScheduler(
            MonitoredApiRepository repository,
            ApiMonitoringService monitoringService
    ) {
        this.repository = repository;
        this.monitoringService = monitoringService;
    }

    @Scheduled(fixedDelay = 60000)
    public void monitorActiveApis() {

        List<MonitoredApi> activeApis =
                repository.findByActiveTrue();

        log.info(
                "API monitoring scheduler started. activeApiCount={}",
                activeApis.size()
        );

        for (MonitoredApi api : activeApis) {

            try {
                monitoringService.checkApi(api);
            } catch (Exception e) {

                log.error(
                        "Unexpected monitoring error. apiId={}",
                        api.getId(),
                        e
                );
            }
        }
    }
}