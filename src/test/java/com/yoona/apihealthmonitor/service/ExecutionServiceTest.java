package com.yoona.apihealthmonitor.service;

import com.yoona.apihealthmonitor.dto.ExecutionCreateRequest;
import com.yoona.apihealthmonitor.dto.MonitoredApiCreateRequest;
import com.yoona.apihealthmonitor.dto.MonitoredApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ExecutionServiceTest {

    @Autowired
    MonitoredApiService monitoredApiService;

    @Autowired
    ExecutionService executionService;

    @Test
    void 성공_실행이력을_저장한다() {

        MonitoredApiResponse api = monitoredApiService.create(
                new MonitoredApiCreateRequest(
                        "회원 API",
                        "https://example.com/api/users",
                        "GET",
                        true
                )
        );

        var response = executionService.record(
                api.id(),
                new ExecutionCreateRequest(
                        true,
                        200,
                        125L,
                        null,
                        null
                )
        );

        assertThat(response.monitoredApiId()).isEqualTo(api.id());
        assertThat(response.success()).isTrue();
        assertThat(response.latencyMs()).isEqualTo(125L);
    }

    @Test
    void 실패_실행에는_에러메시지가_필요하다() {

        MonitoredApiResponse api = monitoredApiService.create(
                new MonitoredApiCreateRequest(
                        "결제 API",
                        "https://example.com/api/payments",
                        "POST",
                        true
                )
        );

        assertThatThrownBy(() ->
                executionService.record(
                        api.id(),
                        new ExecutionCreateRequest(
                                false,
                                500,
                                300L,
                                " ",
                                null
                        )
                )
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 실패한_실행이력만_조회한다() {

        MonitoredApiResponse api = monitoredApiService.create(
                new MonitoredApiCreateRequest(
                        "주문 API",
                        "https://example.com/api/orders",
                        "GET",
                        true
                )
        );

        executionService.record(
                api.id(),
                new ExecutionCreateRequest(
                        true,
                        200,
                        100L,
                        null,
                        null
                )
        );

        executionService.record(
                api.id(),
                new ExecutionCreateRequest(
                        false,
                        500,
                        800L,
                        "Database timeout",
                        null
                )
        );

        var result =
                executionService.findRecent(
                        api.id(),
                        false,
                        0,
                        20
                );

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).success()).isFalse();
        assertThat(result.getContent().get(0).errorMessage())
                .isEqualTo("Database timeout");
    }
}