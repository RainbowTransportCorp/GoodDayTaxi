package com.gooddaytaxi.payment.application.port.out.core.view;

import com.gooddaytaxi.payment.domain.enums.PaymentStatus;

import java.util.UUID;

//검증/권한체크/필요값 추출을 위한 최소한의 결제 정보 조회용 인터페이스
public interface PaymentIdentityView {
    UUID getId();
    UUID getPassengerId();
    UUID getDriverId();
    UUID getTripId();
    PaymentStatus getStatus();
}
