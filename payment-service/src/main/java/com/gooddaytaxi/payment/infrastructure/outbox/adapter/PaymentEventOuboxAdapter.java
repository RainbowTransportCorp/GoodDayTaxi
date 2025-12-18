package com.gooddaytaxi.payment.infrastructure.outbox.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.payment.application.outbox.OutboxEventModel;
import com.gooddaytaxi.payment.application.port.out.event.PaymentEventOutboxPort;
import com.gooddaytaxi.payment.infrastructure.outbox.entity.PaymentEvent;
import com.gooddaytaxi.payment.infrastructure.outbox.exception.PaymentOutboxException;
import com.gooddaytaxi.payment.infrastructure.outbox.payload.ErrorNotificationPayload;
import com.gooddaytaxi.payment.infrastructure.outbox.persistence.PaymentEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventOuboxAdapter implements PaymentEventOutboxPort {
    final PaymentEventRepository repository;
    private final int MAX_RETRY_COUNT = 3;
    private final ObjectMapper objectMapper;

    @Value("${payment.event.payload-version}")
    private int PAYLOAD_VERSION;


    @Override
    public void save(OutboxEventModel model) {
        PaymentEvent event = new PaymentEvent(
                model.eventType(),
                model.topic(),
                model.messageKey(),
                model.aggregateType(),
                model.aggregateId(),
                model.version(),
                model.payloadJson()
        );
        repository.save(event);
    }

    @Override
    public List<OutboxEventModel> findPending(int limit) {
        return repository.findPending(PageRequest.of(0,limit)).stream()
                .map(event -> new OutboxEventModel(
                        event.getId(),
                        event.getType(),
                        event.getTopic(),
                        event.getMessageKey(),
                        event.getAggregateType(),
                        event.getAggregateId(),
                        event.getPayloadVersion(),
                        event.getPayload()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void markPublished(UUID eventId) {
        int updated = repository.updateStatusPublished(eventId);
        // 업데이트된 행이 없으면 경고 로그 출력
        if (updated == 0) {
            log.warn("[PAYMENT-OUTBOX-MARK-PUBLISHED-FAILED] eventId={}", eventId);
        }
    }

    @Override
    @Transactional
    public void markFailed(UUID eventId, String message) {
        PaymentEvent event  = repository.findById(eventId).orElseThrow(() -> new PaymentOutboxException("Outbox event not found: " + eventId));
        //최종 실패시 log를 보내고 끝
        if (event.getType().equals("ERROR_DETECTED")) {
            log.error("[ERROR_EVENT_FAILED] id={} errorEvent publish failed. message={}",
                    eventId, message);
            event.updateStatusToFailed();
            return;
        }
        // retry count 증가 및 마지막 에러 메시지 업데이트
        event.increaseRetryCount();
        event.updateLastErrorMessage(message);
        // 4번째 시도시 에러처리 메시지가 아니라면 상태를 FAILED로 업데이트하는 로직 추가
        if(event.getRetryCount()>=MAX_RETRY_COUNT) {
            event.updateStatusToFailed();

            //새로운 이벤트 발행
            PaymentEvent errorEvent = createOutboxPublishFailedEvent(event,message);
            repository.save(errorEvent);
        }
    }

    private PaymentEvent createOutboxPublishFailedEvent(PaymentEvent original, String errorMessage) {
        return new PaymentEvent(
                "ERROR_DETECTED",
                "error.detected",
                original.getMessageKey(),
                original.getAggregateType(),
                original.getAggregateId(),
                PAYLOAD_VERSION,
                buildErrorPayload(original, errorMessage)
        );
    }

    private String buildErrorPayload(PaymentEvent original, String errorMessage) {
        log.info("Fail Event's Envolpe : {}", original.getPayload());
        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("eventId", UUID.randomUUID());
        envelope.put("eventType","ERROR_DETECTED");
        envelope.put("occurredAt", LocalDateTime.now());
        envelope.put("payloadVersion",PAYLOAD_VERSION);
        Map<String, String> p = parsePayload(original.getPayload());
        ErrorNotificationPayload payload = new ErrorNotificationPayload(
                p.get("notificationOriginId"),
                p.get("notifierId"),

                p.get("tripId"),
                p.get("paymentId"),
                p.get("driverId"),
                p.get("passengerId"),

                original.getType(),
                "PAYMENT_ERROR",
                safe(errorMessage)
        );
        envelope.put("payload",payload);

        try {
            return objectMapper.writeValueAsString(envelope);
        } catch (Exception e) {
            errorMessage = errorMessage == null ? "" : errorMessage.replace("\"", "\\\"");
            // 최악의 경우라도 저장은 되게
            return "{\"error\":\"payload serialization failed\",\"reason\":\"" + errorMessage + "\"}";
        }
    }

    private Map<String, String> parsePayload(String envelopeJson) {
        if (envelopeJson == null || envelopeJson.isBlank()) {
            return new LinkedHashMap<>();
        }
        try {
            // 1) envelope 전체를 Map으로 1회 파싱
            Map<String, Object> envelope = objectMapper.readValue(
                    envelopeJson,
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {}
            );
            // 2) 여기서 payload 추출
            Object payloadObj = envelope.get("payload");
            log.info("Fail Event's Payload : {}", payloadObj);

            //payload가 null값이거나 map이 아닐때
            if (!(payloadObj instanceof Map<?, ?>)) {
                log.warn("[ERROR_PAYLOAD_INVALID] payloadType={}",
                        payloadObj == null ? "null" : payloadObj.getClass());
                return new LinkedHashMap<>();
            }

            Map<String, Object> inner = (Map<String, Object>) payloadObj;

            //3) payload의 값들을 모두 String으로 변환해서 반환
            Map<String, String> result = new LinkedHashMap<>();
            for (Map.Entry<String, Object> e : inner.entrySet()) {
                result.put(
                        e.getKey(),
                        e.getValue() == null ? null : String.valueOf(e.getValue())
                );
            }
            return result;
        } catch (Exception e) {
            log.warn("[ERROR_PAYLOAD_PARSE_FAILED] payloadJson={}", envelopeJson, e);
            return new LinkedHashMap<>();
        }
    }
    private String safe(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }
}
