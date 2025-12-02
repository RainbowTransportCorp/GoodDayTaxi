package com.gooddaytaxi.dispatch.infrastructure.adapter;

import com.gooddaytaxi.dispatch.application.port.out.commend.DispatchCommandPort;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.repository.DispatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DispatchCommandAdapter implements DispatchCommandPort {

    private final DispatchRepository repository;

    @Override
    public Dispatch save(Dispatch dispatchRequest) {
        return repository.save(dispatchRequest);
    }
}