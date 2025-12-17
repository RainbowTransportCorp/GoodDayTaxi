package com.gooddaytaxi.payment.infrastructure.outbox.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.payment.application.outbox.OutboxEventModel;
import com.gooddaytaxi.payment.application.port.out.event.PaymentEventOutboxPort;
import com.gooddaytaxi.payment.infrastructure.outbox.entity.PaymentEvent;
import com.gooddaytaxi.payment.infrastructure.outbox.exception.PaymentOutboxException;
import com.gooddaytaxi.payment.infrastructure.outbox.persistence.PaymentEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        // retry count 증가 및 마지막 에러 메시지 업데이트
        event.increaseRetryCount();
        event.updateLastErrorMessage(message);
        // 4번째 시도시 상태를 FAILED로 업데이트하는 로직 추가
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
                "system.error-domain-events",
                original.getMessageKey(),
                original.getAggregateType(),
                original.getAggregateId(),
                1,
                buildErrorPayload(original, errorMessage)
        );
    }

    private String buildErrorPayload(PaymentEvent original, String errorMessage) {
        try {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("outboxEventId", original.getId());
            m.put("originalType", original.getType());
            m.put("originalTopic", original.getTopic());
            m.put("originalMessageKey", original.getMessageKey());
            m.put("retryCount", original.getRetryCount());
            m.put("lastErrorMessage", errorMessage);
            m.put("originalPayloadVersion", original.getPayloadVersion());
            // 너무 크면 빼도 됨 (운영 로그/테이블 폭 고려)
            m.put("originalPayload", original.getPayload());

            return objectMapper.writeValueAsString(m);
        } catch (Exception e) {
            // 최악의 경우라도 저장은 되게
            return "{\"error\":\"payload serialization failed\",\"reason\":\"" + safe(errorMessage) + "\"}";
        }
    }
    private String safe(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }
}
