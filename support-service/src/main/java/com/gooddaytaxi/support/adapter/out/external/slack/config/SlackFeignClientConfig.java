package com.gooddaytaxi.support.adapter.out.external.slack.config;

import feign.RequestInterceptor;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

/** 메시지 전송 알림을 위한 SlackFeignClient에 대한 Config
 * HTTP - https://docs.slack.dev/reference/methods/chat.postMessage/
 */
@Slf4j
public class SlackFeignClientConfig {

    @Value("${slack.bot-token}")
    private String slackToken;

    /**
     * Slack 호출 시 헤더 자동 추가
     * - Authorization: OAuth Token
     * - Content-Type: application/json 권장
     */
    @Bean
    public RequestInterceptor slackAuthInterceptor() {
        return template -> {
          template.header("Authorization", "Bearer " + slackToken);
          template.header("Content-Type", "application/json; charset=utf-8");
        };
    }

    /**
     * Slack HTTP 레벨 에러에 대한 처리
     * - 429 rate_limited 처리
     * - 그 외 Slack error Logging
     */
    @Bean
    public ErrorDecoder slackErrorDecoder() {
        return new ErrorDecoder.Default() {

            @Override
            public Exception decode(String methodKey, Response response) {
                int status = response.status();

                // Slack rate limiting
                if (status == 429) {
                    String retryAfter = response.headers().getOrDefault("Retry-After", null)
                            .stream().findFirst().orElse("1");

                    log.error("[SlackFeign] Rate limited! Retry after {} seconds", retryAfter);
                    return new RuntimeException("Slack API Rate Limited: retryAfter=" + retryAfter);
                }

                // 그 외 400 이상 HTTP Error
                if (status >= 400) {
                    log.error("[SlackFeign] HTTP error. method={}, status={}", methodKey, status);
                    return new RuntimeException("Slack API HTTP Error: " + status);
                }
                return new ErrorDecoder.Default().decode(methodKey, response);
            }
        };
    }


}
