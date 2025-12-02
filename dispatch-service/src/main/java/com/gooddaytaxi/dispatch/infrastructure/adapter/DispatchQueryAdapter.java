package com.gooddaytaxi.dispatch.infrastructure.adapter;

import com.gooddaytaxi.dispatch.application.exception.DispatchNotFoundException;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.repository.DispatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DispatchQueryAdapter implements DispatchQueryPort {

    private final DispatchRepository dispatchRepository;

    @Override
    public List<Dispatch> findAllByFilter() {
        return dispatchRepository.findAllByCondition();
    }

    @Override
    public Dispatch findById(UUID dispatchId) {
        return dispatchRepository.findById(dispatchId)
                .orElseThrow(()-> new DispatchNotFoundException(dispatchId));
    }
}
