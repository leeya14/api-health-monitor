package com.yoona.apihealthmonitor.controller;

import com.yoona.apihealthmonitor.dto.MonitoredApiCreateRequest;
import com.yoona.apihealthmonitor.dto.MonitoredApiResponse;
import com.yoona.apihealthmonitor.dto.MonitoredApiUpdateRequest;
import com.yoona.apihealthmonitor.service.MonitoredApiService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitored-apis")
public class MonitoredApiController {
    private final MonitoredApiService service;

    public MonitoredApiController(MonitoredApiService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MonitoredApiResponse> create(@Valid @RequestBody MonitoredApiCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public List<MonitoredApiResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public MonitoredApiResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public MonitoredApiResponse update(@PathVariable Long id, @Valid @RequestBody MonitoredApiUpdateRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
