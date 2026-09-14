package com.yoona.apihealthmonitor.scheduler;

import com.yoona.apihealthmonitor.service.DailyStatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DailyStatScheduler {
    private static final Logger log = LoggerFactory.getLogger(DailyStatScheduler.class);
    private final DailyStatService service;

    public DailyStatScheduler(DailyStatService service) {
        this.service = service;
    }

    // 매일 00:10에 전날 통계를 집계한다.
    @Scheduled(cron = "0 10 0 * * *", zone = "Asia/Seoul")
    public void aggregateYesterday() {
        LocalDate targetDate = LocalDate.now().minusDays(1);
        try {
            int processed = service.aggregateDate(targetDate);
            log.info("daily-stat aggregation completed. date={}, processedApiCount={}", targetDate, processed);
        } catch (Exception e) {
            log.error("daily-stat aggregation failed. date={}", targetDate, e);
        }
    }
}
