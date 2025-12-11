package com.gooddaytaxi.support.adapter.out.internal.account;

import com.gooddaytaxi.support.adapter.out.internal.account.dto.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/* Account Domain과 통신하기 위한 Feign Client
*
*/
@FeignClient(name = "account-service")
public interface AccountFeignClient {

    @GetMapping("/internal/api/v1/users/{userId}")
    UserInfo getUserInfo(@PathVariable("userId") String userId);
}

