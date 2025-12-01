package com.gooddaytaxi.dispatch.infrastructure.repository;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.repository.DispatchRepositoryCustom;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DispatchRepositoryImpl implements DispatchRepositoryCustom {

    private final EntityManager em;


    @Override
    public List<Dispatch> findAllByCondition() {
        return List.of();
    }

    @Override
    public Optional<Dispatch> findById(UUID id) {
        return Optional.ofNullable(em.find(Dispatch.class, id));
    }
}