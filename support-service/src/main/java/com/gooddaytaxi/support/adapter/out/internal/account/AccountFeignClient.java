package com.gooddaytaxi.support.adapter.out.internal.account;

import com.gooddaytaxi.support.adapter.out.internal.account.dto.DriverProfile;
import com.gooddaytaxi.support.adapter.out.internal.account.dto.UserProfile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

/* Account Domain과 통신하기 위한 Feign Client
*
*/
@FeignClient(name = "account-service", fallback = AccountFeignClientFallback.class)
public interface AccountFeignClient {

    @GetMapping("/internal/v1/account/users/{userId}")
    UserProfile getUserInfo(@PathVariable("userId") String userId);

    @GetMapping("/internal/v1/account/drivers/{userId}")
    DriverProfile getDriverInfo(@PathVariable("userId") String userId);

    @GetMapping("/internal/v1/account/admins/uuids")
    List<UUID> getMasterAdminUuids();

}