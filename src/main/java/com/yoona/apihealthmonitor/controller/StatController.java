package com.yoona.apihealthmonitor.controller;

import com.yoona.apihealthmonitor.dto.DailyStatResponse;
import com.yoona.apihealthmonitor.service.DailyStatService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
public class StatController {
    private final DailyStatService service;

    public StatController(DailyStatService service) {
        this.service = service;
    }

    @GetMapping("/api/monitored-apis/{apiId}/stats/daily")
    public List<DailyStatResponse> findStats(@PathVariable Long apiId,
                                             @RequestParam LocalDate from,
                                             @RequestParam LocalDate to) {
        return service.findStats(apiId, from, to);
    }

    @PostMapping("/api/monitored-apis/{apiId}/stats/daily/{date}/aggregate")
    public DailyStatResponse aggregateOne(@PathVariable Long apiId, @PathVariable LocalDate date) {
        return service.aggregateApi(apiId, date);
    }

    @PostMapping("/api/admin/stats/daily/{date}/aggregate")
    public Map<String, Object> aggregateAll(@PathVariable LocalDate date) {
        return Map.of("date", date, "processedApiCount", service.aggregateDate(date));
    }
}
