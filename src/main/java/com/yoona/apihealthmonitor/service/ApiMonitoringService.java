package com.yoona.apihealthmonitor.service;

import com.yoona.apihealthmonitor.dto.ExecutionCreateRequest;
import com.yoona.apihealthmonitor.entity.MonitoredApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

@Service
public class ApiMonitoringService {

    private static final Logger log =
            LoggerFactory.getLogger(ApiMonitoringService.class);

    private final RestClient restClient;
    private final ExecutionService executionService;

    public ApiMonitoringService(
            ExecutionService executionService
    ) {
        this.restClient = RestClient.create();
        this.executionService = executionService;
    }

    public void checkApi(MonitoredApi api) {

        long startTime = System.currentTimeMillis();

        try {

            ResponseEntity<String> response =
                    restClient
                            .method(
                                    HttpMethod.valueOf(
                                            api.getHttpMethod()
                                    )
                            )
                            .uri(api.getEndpointUrl())
                            .retrieve()
                            .toEntity(String.class);

            long latency =
                    System.currentTimeMillis() - startTime;

            int statusCode =
                    response.getStatusCode().value();

            saveExecution(
                    api,
                    true,
                    statusCode,
                    latency,
                    null
            );

            log.info(
                    "API monitoring success. apiId={}, name={}, status={}, latency={}ms",
                    api.getId(),
                    api.getName(),
                    statusCode,
                    latency
            );

        } catch (HttpStatusCodeException e) {

            long latency =
                    System.currentTimeMillis() - startTime;

            int statusCode =
                    e.getStatusCode().value();

            saveExecution(
                    api,
                    false,
                    statusCode,
                    latency,
                    "HTTP status: " + statusCode
            );

            log.warn(
                    "API monitoring HTTP failure. apiId={}, name={}, status={}, latency={}ms",
                    api.getId(),
                    api.getName(),
                    statusCode,
                    latency
            );

        } catch (Exception e) {

            long latency =
                    System.currentTimeMillis() - startTime;

            saveExecution(
                    api,
                    false,
                    null,
                    latency,
                    getErrorMessage(e)
            );

            log.warn(
                    "API monitoring connection failure. apiId={}, name={}, error={}",
                    api.getId(),
                    api.getName(),
                    e.getMessage()
            );
        }
    }

    private void saveExecution(
            MonitoredApi api,
            boolean success,
            Integer statusCode,
            long latency,
            String errorMessage
    ) {

        executionService.record(
                api.getId(),
                new ExecutionCreateRequest(
                        success,
                        statusCode,
                        latency,
                        errorMessage,
                        null
                )
        );
    }

    private String getErrorMessage(Exception e) {

        String message = e.getMessage();

        if (message == null || message.isBlank()) {
            return e.getClass().getSimpleName();
        }

        return message.length() > 1000
                ? message.substring(0, 1000)
                : message;
    }
}