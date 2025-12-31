package com.gooddaytaxi.support.adapter.out.external.slack.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Slack API 성공/실패 Response
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SlackMessageAPIRes(
        boolean ok,
        String error,
        String channel,
        String ts
) {}

