package com.gooddaytaxi.dispatch.application.port.out.query;

import com.gooddaytaxi.dispatch.infrastructure.client.account.dto.DriverInfo;

public interface AccountDriverSelectionQueryPort {

    /**
     * DriverInfo는 Account-service와의 통신을 위한 공식 응답 모델(Integration DTO)입니다.
     * 외부 서비스와의 '계약 모델'이므로 Application Port에서 사용해도 계층 위반이 아닙니다.
     * (Port는 외부 시스템의 응답 형태를 표현할 수 있음)
     */
    DriverInfo getAvailableDrivers(String pickupAddress);
}
