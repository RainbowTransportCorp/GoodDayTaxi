package com.gooddaytaxi.support.adapter.out.internal.account;

import com.gooddaytaxi.support.adapter.out.internal.account.dto.DriverProfile;
import com.gooddaytaxi.support.adapter.out.internal.account.dto.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class AccountFeignClientFallback implements AccountFeignClient {
    @Override
    public UserProfile getUserInfo(String userId) {
        log.error("❌ [User Profile] AccountFeignClient fallback triggered userId={}", userId);
        return null;
    }

    @Override
    public DriverProfile getDriverInfo(String userId) {
        log.error("❌ [Driver Profile] AccountFeignClient fallback triggered userId={}", userId);
        return null;
    }

    @Override
    public List<UUID> getMasterAdminUuids() {
        log.error("❌ [Master Admin User] AccountFeignClient fallback triggered role=MASTER_ADMIN");
        return null;
    }
}

