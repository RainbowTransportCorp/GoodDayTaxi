package com.gooddaytaxi.dispatch.domain.repository;

import com.gooddaytaxi.dispatch.domain.model.entity.DispatchEvent;

public interface DispatchEventRepositoryCustom {
    DispatchEvent save(DispatchEvent event);
}
