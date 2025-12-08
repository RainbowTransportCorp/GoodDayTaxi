package com.gooddaytaxi.support.adapter.out.external.slack;

import com.gooddaytaxi.support.adapter.out.external.slack.config.SlackFeignClientConfig;
import com.gooddaytaxi.support.adapter.out.external.slack.dto.SlackMessageAPIRes;
import com.gooddaytaxi.support.adapter.out.external.slack.dto.SlackMessageAlertReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "slack-client",
        url = "${slack.api-url}",
        configuration = SlackFeignClientConfig.class
)
public interface SlackFeignClient {

    @PostMapping("/api/chat.postMessage")
    SlackMessageAPIRes postMessage(@RequestBody SlackMessageAlertReq request);
}
