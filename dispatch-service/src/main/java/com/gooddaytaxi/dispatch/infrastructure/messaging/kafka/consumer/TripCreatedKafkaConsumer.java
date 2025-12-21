package com.gooddaytaxi.dispatch.infrastructure.messaging.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;;
import com.gooddaytaxi.dispatch.application.port.in.command.TripReadyCommandPort;
import com.gooddaytaxi.dispatch.application.service.dispatch.DispatchTripRequestMonitor;
import com.gooddaytaxi.dispatch.application.service.dispatch.TripReadyDispatchService;
import com.gooddaytaxi.dispatch.infrastructure.messaging.kafka.consumer.dto.TripReadyConsumedEvent;;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TripCreatedKafkaConsumer implements TripReadyCommandPort {

    private final ObjectMapper objectMapper;
    private final TripReadyDispatchService tripReadyDispatchService;
    private final DispatchTripRequestMonitor tripRequestMonitor;

    @KafkaListener(topics = "trip.ready", groupId = "dispatch-group")
    public void consume(String message) {
        try {
            TripReadyConsumedEvent event =
                objectMapper.readValue(message, TripReadyConsumedEvent.class);

            onTripReady(
                event.payload().dispatchId(),
                event.payload().tripId(),
                event.payload().startTime()
            );

            log.info(
                "[DISPATCH-TRIP-STARTED] eventId={} dispatchId={}",
                event.eventId(),
                event.payload().dispatchId()
            );

        } catch (Exception ex) {
            log.error(
                "[DISPATCH-TRIP-STARTED-ERROR] payload={} error={}",
                message,
                ex.getMessage()
            );
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void onTripReady(UUID dispatchId, UUID tripId,LocalDateTime createdAt) {
        tripReadyDispatchService.onTripReady(dispatchId, createdAt);

        //이벤트 구독이 정상적으로 마무리 되었으므로 해당 dispatch의 모니터링 중단
        tripRequestMonitor.clear(dispatchId);
    }
}
