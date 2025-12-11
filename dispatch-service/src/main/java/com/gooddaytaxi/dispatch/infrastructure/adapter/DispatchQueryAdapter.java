package com.gooddaytaxi.dispatch.infrastructure.adapter;

import com.gooddaytaxi.dispatch.application.exception.DispatchNotFoundException;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
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
    public List<Dispatch> findAllByPassengerId(UUID passengerId) {
        return dispatchRepository.findAllByPassengerId(passengerId);
    }

    @Override
    public Dispatch findById(UUID dispatchId) {
        return dispatchRepository.findByDispatchId(dispatchId)
                .orElseThrow(()-> new DispatchNotFoundException(dispatchId));
    }

    @Override
    public List<Dispatch> findByStatus(DispatchStatus status) {
        return dispatchRepository.findByStatus(status);
    }

    @Override
    public List<Dispatch> findTimeoutTargets(int seconds) {
        return dispatchRepository.findTimeoutTargets(seconds);
    }
}
