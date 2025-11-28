package com.gooddaytaxi.dispatch.infrastructure.repository;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.repository.DispatchRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DispatchRequestRepositoryImpl implements DispatchRepository {

    private final EntityManager em;

    @Override
    public Dispatch save(Dispatch request) {
        em.persist(request);
        return request;
    }

    @Override
    public Optional<Dispatch> findById(UUID id) {
        return Optional.ofNullable(em.find(Dispatch.class, id));
    }
}