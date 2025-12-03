package com.gooddaytaxi.trip.infrastructure.adapter;

import com.gooddaytaxi.trip.application.port.out.CreateFarePolicyPort;
import com.gooddaytaxi.trip.domain.model.FarePolicy;
import com.gooddaytaxi.trip.infrastructure.persistence.FarePolicyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.sound.sampled.Port;

@Component
@RequiredArgsConstructor
public class FarePolicyPersistenceAdapter implements CreateFarePolicyPort {

    private final FarePolicyJpaRepository jpaRepository;

    @Override
    public FarePolicy create(FarePolicy farePolicy) {
        return jpaRepository.save(farePolicy); // 실제 DB 작업을 수행합니다.
    }
}
