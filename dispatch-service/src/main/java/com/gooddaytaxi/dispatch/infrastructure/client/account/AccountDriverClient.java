package com.gooddaytaxi.dispatch.infrastructure.client.account;

import com.gooddaytaxi.dispatch.infrastructure.client.account.dto.DriverInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "account-service", url = "${service.account.url}") //로컬 -> 도커 사용시 url 필수
public interface AccountDriverClient {

    /**
     * 출발지 기준으로 배차 가능한 기사 목록을 조회한다.
     *
     * <Account 서비스 처리 범위>
     * - 지역 기반 기사 필터링
     * - 기사 온라인 상태 / 배차 가능 여부 판단
     * - 정책에 따른 기사 리스트 구성
     *
     * @param pickupAddress 승객의 출발지 주소
     * @return Account 서비스에서 필터링된 배차 가능 기사 정보
     */
    @GetMapping("/internal/v1/account/drivers/available")
    DriverInfo getAvailableDrivers(@RequestParam String pickupAddress);
}

