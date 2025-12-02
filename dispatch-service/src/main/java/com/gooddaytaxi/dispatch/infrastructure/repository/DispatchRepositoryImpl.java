package com.gooddaytaxi.dispatch.infrastructure.repository;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.repository.DispatchRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DispatchRepositoryImpl implements DispatchRepositoryCustom {
    @Override
    public List<Dispatch> findAllByCondition() {
        return List.of();
    }

    @Override
    public Optional<Dispatch> findByDispatchId(UUID id) {
        return Optional.empty();
    }
}