package com.yoona.apihealthmonitor.service;

import com.yoona.apihealthmonitor.dto.ExecutionCreateRequest;
import com.yoona.apihealthmonitor.dto.ExecutionResponse;
import com.yoona.apihealthmonitor.entity.ApiExecutionLog;
import com.yoona.apihealthmonitor.entity.MonitoredApi;
import com.yoona.apihealthmonitor.repository.ApiExecutionLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ExecutionService {

    private final ApiExecutionLogRepository repository;
    private final MonitoredApiService monitoredApiService;

    public ExecutionService(ApiExecutionLogRepository repository,
                            MonitoredApiService monitoredApiService) {
        this.repository = repository;
        this.monitoredApiService = monitoredApiService;
    }

    @Transactional
    public ExecutionResponse record(Long apiId, ExecutionCreateRequest request) {
        MonitoredApi api = monitoredApiService.getEntity(apiId);
        validate(request);

        ApiExecutionLog log = new ApiExecutionLog();
        log.setMonitoredApi(api);
        log.setSuccess(request.success());
        log.setHttpStatus(request.httpStatus());
        log.setLatencyMs(request.latencyMs());
        log.setErrorMessage(normalizeErrorMessage(request));
        log.setExecutedAt(request.executedAt());

        return toResponse(repository.save(log));
    }

    public Page<ExecutionResponse> findRecent(Long apiId,
                                              Boolean success,
                                              int page,
                                              int size) {

        monitoredApiService.getEntity(apiId);

        Pageable pageable =
                PageRequest.of(page, Math.min(Math.max(size, 1), 100));

        if (success == null) {
            return repository
                    .findByMonitoredApiIdOrderByExecutedAtDesc(apiId, pageable)
                    .map(this::toResponse);
        }

        return repository
                .findByMonitoredApiIdAndSuccessOrderByExecutedAtDesc(
                        apiId,
                        success,
                        pageable
                )
                .map(this::toResponse);
    }

    private void validate(ExecutionCreateRequest request) {

        if (Boolean.TRUE.equals(request.success())
                && request.httpStatus() != null
                && request.httpStatus() >= 400) {
            throw new IllegalArgumentException(
                    "성공 실행의 HTTP 상태 코드는 400 미만이어야 합니다."
            );
        }

        if (Boolean.FALSE.equals(request.success())
                && (request.errorMessage() == null
                || request.errorMessage().isBlank())) {
            throw new IllegalArgumentException(
                    "실패 실행에는 errorMessage가 필요합니다."
            );
        }
    }

    private String normalizeErrorMessage(ExecutionCreateRequest request) {
        return Boolean.TRUE.equals(request.success())
                ? null
                : request.errorMessage().trim();
    }

    private ExecutionResponse toResponse(ApiExecutionLog log) {

        return new ExecutionResponse(
                log.getId(),
                log.getMonitoredApi().getId(),
                log.isSuccess(),
                log.getHttpStatus(),
                log.getLatencyMs(),
                log.getErrorMessage(),
                log.getExecutedAt()
        );
    }
}