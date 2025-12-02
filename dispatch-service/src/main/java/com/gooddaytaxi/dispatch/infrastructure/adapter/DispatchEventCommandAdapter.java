package com.gooddaytaxi.dispatch.infrastructure.adapter;

import com.gooddaytaxi.dispatch.application.port.out.commend.DispatchEventCommandPort;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchEvent;
import com.gooddaytaxi.dispatch.domain.repository.DispatchEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DispatchEventCommandAdapter implements DispatchEventCommandPort {

    private final DispatchEventRepository repository;

    @Override
    public void save(DispatchEvent event) {
        repository.save(event);
    }
}
