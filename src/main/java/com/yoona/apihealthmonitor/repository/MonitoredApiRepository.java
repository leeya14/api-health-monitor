package com.yoona.apihealthmonitor.repository;

import com.yoona.apihealthmonitor.entity.MonitoredApi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonitoredApiRepository extends JpaRepository<MonitoredApi, Long> {

    boolean existsByNameIgnoreCase(String name);

    List<MonitoredApi> findByActiveTrue();
}