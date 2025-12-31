package com.gooddaytaxi.trip.infrastructure.adapter;

import com.gooddaytaxi.trip.application.port.out.CreateFarePolicyPort;
import com.gooddaytaxi.trip.application.port.out.LoadActiveFarePolicyPort;
import com.gooddaytaxi.trip.domain.model.FarePolicy;
import com.gooddaytaxi.trip.infrastructure.persistence.FarePolicyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.sound.sampled.Port;

@Component
@RequiredArgsConstructor
public class FarePolicyPersistenceAdapter implements CreateFarePolicyPort, LoadActiveFarePolicyPort {

    private final FarePolicyJpaRepository jpaRepository;

    @Override
    public FarePolicy create(FarePolicy farePolicy) {
        return jpaRepository.save(farePolicy); // 실제 DB 작업을 수행합니다.
    }

    @Override
    public FarePolicy loadActivePolicy() {
        return jpaRepository.findByIsActiveTrue()
                .orElseThrow(() ->
                        new IllegalStateException("활성 요금 정책이 존재하지 않습니다"));
    }
}
