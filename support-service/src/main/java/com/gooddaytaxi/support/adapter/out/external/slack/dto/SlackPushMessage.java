//package com.gooddaytaxi.support.adapter.out.external.slack.dto;
//
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.gooddaytaxi.support.application.port.out.dto.Message;
//import lombok.Getter;
//
//import java.util.List;
//import java.util.UUID;
//
///**
// * Push 메시지 DTO
// * - Slack으로 Push하는 메시지
// * - slackTargets: index: 0 -> driver의 Slack ID, 1 -> passenger의 Slack ID
// */
//@Getter
//public class SlackPushMessage extends Message {
//
//    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
//    private final List<String> slackTargets;
//
//    @JsonCreator
//    private SlackPushMessage(
//            @JsonProperty("slackTargets") List<String> slackTargets,
//            @JsonProperty("title") String title,
//            @JsonProperty("body") String body
//    ) {
//        super(title, body);
//        this.slackTargets = slackTargets;
//    }
//    public static SlackPushMessage create(List<String> slackTargets, String title, String body) {
//        return new SlackPushMessage(slackTargets, title, body);
//    }
//
//    public static SlackPushMessage from(String data) {
//        try {
//            return mapper.readValue(data, SlackPushMessage.class);
//        } catch (Exception e) {
//            throw new RuntimeException("Invalid JSON for SlackPushMessage", e);
//        }
//    }
//}