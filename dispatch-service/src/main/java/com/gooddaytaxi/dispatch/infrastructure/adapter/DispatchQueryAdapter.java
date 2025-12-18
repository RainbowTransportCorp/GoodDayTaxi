package com.gooddaytaxi.dispatch.infrastructure.adapter;

import com.gooddaytaxi.dispatch.application.exception.DispatchNotFoundException;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.domain.repository.DispatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Dispatch 조회를 위한 Query Adapter.
 *
 * Application 계층에서 사용하는 DispatchQueryPort를 구현하며,
 * 실제 데이터 조회는 DispatchRepository에 위임한다.
 */
@Component
@RequiredArgsConstructor
public class DispatchQueryAdapter implements DispatchQueryPort {

    private final DispatchRepository dispatchRepository;

    @Override
    public List<Dispatch> findAll() {
        return dispatchRepository.findAll();
    }

    @Override
    public List<Dispatch> findByStatus(DispatchStatus status) {
        return dispatchRepository.findByStatus(status);
    }

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
    public Optional<Dispatch> findByIdAndPassengerId(UUID dispatchId, UUID passengerId) {
        return dispatchRepository.findByIdAndPassengerId(dispatchId,passengerId);
    }

    @Override
    public List<Dispatch> findTimeoutCandidates() {
        return dispatchRepository.findTimeoutCandidates();
    }
}
