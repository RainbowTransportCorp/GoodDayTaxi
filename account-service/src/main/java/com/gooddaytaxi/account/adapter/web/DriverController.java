package com.gooddaytaxi.account.adapter.web;

import com.gooddaytaxi.account.application.dto.DriverProfileResponse;
import com.gooddaytaxi.account.application.usecase.GetDriverProfileUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {
    
    private final GetDriverProfileUseCase getDriverProfileUseCase;
    
    @GetMapping("/{driverId}")
    public ResponseEntity<DriverProfileResponse> getDriverProfile(
            @PathVariable("driverId") String driverId) {
        
        log.debug("기사 프로필 조회 요청: driverId={}", driverId);
        
        UUID driverUuid = UUID.fromString(driverId);
        DriverProfileResponse response = getDriverProfileUseCase.execute(driverUuid);
        
        log.debug("기사 프로필 조회 완료: driverId={}", driverId);
        
        return ResponseEntity.ok(response);
    }
}