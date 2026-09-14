package com.yoona.apihealthmonitor.repository;

import com.yoona.apihealthmonitor.entity.DailyApiStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyApiStatRepository extends JpaRepository<DailyApiStat, Long> {
    Optional<DailyApiStat> findByMonitoredApiIdAndStatDate(Long monitoredApiId, LocalDate statDate);
    List<DailyApiStat> findByMonitoredApiIdAndStatDateBetweenOrderByStatDateDesc(Long monitoredApiId, LocalDate from, LocalDate to);
}
