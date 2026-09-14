package com.yoona.apihealthmonitor.repository;

import com.yoona.apihealthmonitor.entity.MonitoredApi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonitoredApiRepository extends JpaRepository<MonitoredApi, Long> {
    boolean existsByNameIgnoreCase(String name);
}
