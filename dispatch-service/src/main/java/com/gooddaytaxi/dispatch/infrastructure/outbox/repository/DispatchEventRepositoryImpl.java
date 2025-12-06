package com.gooddaytaxi.dispatch.infrastructure.outbox.repository;

import com.gooddaytaxi.dispatch.application.port.out.commend.DispatchEventRepository;
import com.gooddaytaxi.dispatch.infrastructure.outbox.entity.DispatchEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DispatchEventRepositoryImpl implements DispatchEventRepository {

    private final DispatchEventJpaRepository dispatchEventJpaRepository;

    @Override
    public DispatchEvent save(DispatchEvent event) {
        return dispatchEventJpaRepository.save(event);
    }
}
