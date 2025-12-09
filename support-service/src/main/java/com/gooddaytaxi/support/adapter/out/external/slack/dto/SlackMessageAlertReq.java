package com.gooddaytaxi.support.adapter.out.external.slack.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;

import java.util.List;
import java.util.Map;

/** Slack Message 알림창 내용 구성을 위한 Request
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record SlackMessageAlertReq(
        String channel,
        String text,
        Boolean mrkdwn,
        List<Map<String, Object>> blocks,
        String username,
        String icon_emoji
) {
    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public static SlackMessageAlertReq from(String data) {
        try {
            return mapper.readValue(data, SlackMessageAlertReq.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON for DispatchRequestReq", e);
        }
    }
}