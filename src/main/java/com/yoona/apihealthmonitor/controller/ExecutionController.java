package com.yoona.apihealthmonitor.controller;

import com.yoona.apihealthmonitor.dto.ExecutionCreateRequest;
import com.yoona.apihealthmonitor.dto.ExecutionResponse;
import com.yoona.apihealthmonitor.service.ExecutionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/monitored-apis/{apiId}/executions")
public class ExecutionController {

    private final ExecutionService service;

    public ExecutionController(ExecutionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ExecutionResponse> record(
            @PathVariable Long apiId,
            @Valid @RequestBody ExecutionCreateRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.record(apiId, request));
    }

    @GetMapping
    public Page<ExecutionResponse> findRecent(
            @PathVariable Long apiId,
            @RequestParam(required = false) Boolean success,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return service.findRecent(apiId, success, page, size);
    }
}