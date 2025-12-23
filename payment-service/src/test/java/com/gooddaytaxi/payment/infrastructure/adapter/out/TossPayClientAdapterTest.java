package com.gooddaytaxi.payment.infrastructure.adapter.out;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.payment.application.command.payment.ExternalPaymentConfirmCommand;
import com.gooddaytaxi.payment.application.command.refund.ExternalPaymentCancelCommand;
import com.gooddaytaxi.payment.application.result.payment.ExternalPaymentConfirmResult;
import com.gooddaytaxi.payment.application.result.refund.ExternalPaymentCancelResult;
import com.gooddaytaxi.payment.infrastructure.client.TosspayFeignClient;
import com.gooddaytaxi.payment.infrastructure.client.dto.TossPayCancelRequestDto;
import com.gooddaytaxi.payment.infrastructure.client.dto.TossPayConfirmRequestDto;
import com.gooddaytaxi.payment.infrastructure.client.dto.TossPayConfirmResponseDto;
import feign.FeignException;
import feign.Request;
import feign.RetryableException;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class TossPayClientAdapterTest {

    private TosspayFeignClient tosspayFeignClient;
    private TossPayClientAdapter adapter;

    @BeforeEach
    void setUp() {
        tosspayFeignClient = Mockito.mock(TosspayFeignClient.class);
        adapter = new TossPayClientAdapter(tosspayFeignClient, new ObjectMapper());
    }

    @Test
    void cancel_connectionRefused_should_map_to_status_minus4() {
        // given
        RetryableException ex = retryableEx(
                "Connection refused: no further information executing POST http://127.0.0.1:59999/v1/payments/xxx/cancel"
        );
        when(tosspayFeignClient.cancelPayment(anyString(), anyString(), any(TossPayCancelRequestDto.class)))
                .thenThrow(ex);

        // when
        ExternalPaymentCancelResult result = adapter.cancelTosspayPayment(
                "paymentKey",
                "idemKey",
                new ExternalPaymentCancelCommand("테스트취소")
        );

        // then
        assertThat(result.success()).isFalse();
        assertThat(result.error()).isNotNull();
        assertThat(result.error().status()).isEqualTo(-4);
        assertThat(result.error().providerMessage()).containsIgnoringCase("Connection refused");
    }

    @Test
    void cancel_connectTimeout_should_map_to_status_minus2() {
        // given
        RetryableException ex = retryableEx("connect timed out executing POST https://api.tosspayments.com/v1/payments/xxx/cancel");
        when(tosspayFeignClient.cancelPayment(anyString(), anyString(), any(TossPayCancelRequestDto.class)))
                .thenThrow(ex);

        // when
        ExternalPaymentCancelResult result = adapter.cancelTosspayPayment(
                "paymentKey",
                "idemKey",
                new ExternalPaymentCancelCommand("테스트취소")
        );

        // then
        assertThat(result.success()).isFalse();
        assertThat(result.error()).isNotNull();
        assertThat(result.error().status()).isEqualTo(-2);
        assertThat(result.error().providerMessage()).containsIgnoringCase("connect");
    }

    @Test
    void cancel_readTimeout_should_map_to_status_minus3() {
        // given
        RetryableException ex = retryableEx("Read timed out executing POST https://api.tosspayments.com/v1/payments/xxx/cancel");
        when(tosspayFeignClient.cancelPayment(anyString(), anyString(), any(TossPayCancelRequestDto.class)))
                .thenThrow(ex);

        // when
        ExternalPaymentCancelResult result = adapter.cancelTosspayPayment(
                "paymentKey",
                "idemKey",
                new ExternalPaymentCancelCommand("테스트취소")
        );

        // then
        assertThat(result.success()).isFalse();
        assertThat(result.error()).isNotNull();
        assertThat(result.error().status()).isEqualTo(-3);
        assertThat(result.error().providerMessage()).containsIgnoringCase("read");
    }

    @Test
    void confirm_httpError_with_toss_json_should_parse_message() {
        // given: 토스 에러 JSON (필드명이 TossErrorResponse와 맞아야 함)
        String tossErrorJson = "{\"message\":\"이미 취소된 결제입니다.\"}";

        FeignException ex = badRequestEx("POST", "https://api.tosspayments.com/v1/payments/confirm", tossErrorJson);

        when(tosspayFeignClient.confirmPayment(anyString(), any(TossPayConfirmRequestDto.class)))
                .thenThrow(ex);

        // when
        ExternalPaymentConfirmResult result = adapter.confirm(
                "idemKey",
                new ExternalPaymentConfirmCommand("paymentKey", "orderId", 1000L)
        );

        // then
        assertThat(result.success()).isFalse();
        assertThat(result.error()).isNotNull();
        assertThat(result.error().status()).isEqualTo(400);
        // providerMessage는 TossErrorResponse.message()로 세팅됨
        assertThat(result.error().providerMessage()).contains("이미 취소된 결제");
    }

    @Test
    void getPayment_httpError_with_nonJsonBody_should_fallback_to_unknown_error() {
        // given: JSON이 아닌 body
        String body = "NOT_JSON";
        FeignException ex = badRequestEx("GET", "https://api.tosspayments.com/v1/payments/xxx", body);

        when(tosspayFeignClient.getPayment(anyString()))
                .thenThrow(ex);

        // when
        ExternalPaymentConfirmResult result = adapter.getPayment("paymentKey");

        // then
        assertThat(result.success()).isFalse();
        assertThat(result.error()).isNotNull();
        assertThat(result.error().status()).isEqualTo(400);
        assertThat(result.error().providerMessage()).isEqualTo("Unknown error");
        assertThat(result.error().rawBody()).contains("NOT_JSON");
    }

    @Test
    void confirm_success_should_return_success_true() {
        // given
        String tossTime =
                OffsetDateTime.now()
                        .withNano(0)                 // 밀리초 제거
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));

        TossPayConfirmResponseDto response = new TossPayConfirmResponseDto(
                UUID.randomUUID().toString(),
                "DONE",
                tossTime,
                tossTime,
                "TOSS_PAY",
                new TossPayConfirmResponseDto.EasyPay("TOSS_PAY"),
                1000
        );

        when(tosspayFeignClient.confirmPayment(anyString(), any(TossPayConfirmRequestDto.class)))
                .thenReturn(response);

        // when
        ExternalPaymentConfirmResult result = adapter.confirm(
                "idemKey",
                new ExternalPaymentConfirmCommand("paymentKey", "orderId", 1000L)
        );

        // then
        assertThat(result.success()).isTrue();
        assertThat(result.error()).isNull();
        assertThat(result.totalAmount()).isEqualTo(1000);
    }

    // -----------------------
    // Helpers
    // -----------------------

    private RetryableException retryableEx(String message) {
        Request request = Request.create(
                Request.HttpMethod.POST,
                "http://127.0.0.1:59999/test",
                Collections.emptyMap(),
                null,
                StandardCharsets.UTF_8,
                null
        );

        // Feign 버전에 따라 생성자 시그니처가 조금 다를 수 있어
        // 이 형태가 컴파일이 안 되면, 에러 메시지에 맞춰 생성자만 바꿔주면 됨.
        return new RetryableException(
                0,
                message,
                request.httpMethod(),
                (Long) null,
                request
        );
    }

    private FeignException badRequestEx(String method, String url, String body) {
        Request request = Request.create(
                Request.HttpMethod.valueOf(method),
                url,
                Map.of("Content-Type", Collections.singletonList("application/json")),
                null,
                StandardCharsets.UTF_8,
                null
        );

        Response response = Response.builder()
                .status(400)
                .reason("Bad Request")
                .request(request)
                .headers(Collections.emptyMap())
                .body(body, StandardCharsets.UTF_8)
                .build();

        return FeignException.errorStatus("tosspay", response);
    }
}
