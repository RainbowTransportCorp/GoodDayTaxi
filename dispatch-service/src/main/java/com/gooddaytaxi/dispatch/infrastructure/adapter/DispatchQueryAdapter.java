package com.gooddaytaxi.dispatch.infrastructure.adapter;

import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.repository.DispatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DispatchQueryAdapter implements DispatchQueryPort {

    private final DispatchRepository dispatchRepository;

    @Override
    public List<Dispatch> findAllByFilter() {
        return dispatchRepository.findAllByCondition();
    }

    @Override //(단건조회 할 때 수정 必)
    public Optional<Dispatch> findById(UUID dispatchId) {
        return Optional.empty();
    }
}
