package com.gooddaytaxi.dispatch.infrastructure.repository;

import com.gooddaytaxi.dispatch.domain.model.entity.DispatchEvent;
import com.gooddaytaxi.dispatch.domain.repository.DispatchEventRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DispatchEventRepositoryImpl implements DispatchEventRepositoryCustom {

    @Override
    public DispatchEvent save(DispatchEvent event) {
        return null;
    }
}

