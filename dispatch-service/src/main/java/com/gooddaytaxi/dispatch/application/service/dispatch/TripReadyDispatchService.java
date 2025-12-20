package com.gooddaytaxi.dispatch.application.service.dispatch;

import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TripReadyDispatchService {

    private final DispatchQueryPort queryPort;
    private final DispatchCommandPort commandPort;

    public void onTripReady(UUID dispatchId, LocalDateTime readyAt) {
        Dispatch dispatch = queryPort.findById(dispatchId);

        dispatch.markTripReady();   // ACCEPTED or TRIP_REQUEST → TRIP_READY
        commandPort.save(dispatch);
    }
}
