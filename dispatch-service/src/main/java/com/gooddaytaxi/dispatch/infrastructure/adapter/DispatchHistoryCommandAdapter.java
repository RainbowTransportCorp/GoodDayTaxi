package com.gooddaytaxi.dispatch.infrastructure.adapter;

import com.gooddaytaxi.dispatch.application.port.out.commend.DispatchHistoryCommandPort;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchHistory;
import com.gooddaytaxi.dispatch.domain.repository.DispatchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class DispatchHistoryCommandAdapter implements DispatchHistoryCommandPort {

    private final DispatchHistoryRepository dispatchHistoryRepository;

    @Override
    public DispatchHistory save(DispatchHistory history) {
        return dispatchHistoryRepository.save(history);
    }
}
