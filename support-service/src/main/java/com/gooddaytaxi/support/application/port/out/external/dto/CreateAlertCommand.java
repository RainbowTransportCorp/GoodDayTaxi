//package com.gooddaytaxi.support.application.port.out.external.dto;
//
//import lombok.Getter;
//
///**
// * Notification Create Command - 알림창 내용 생성 Command
// */
//@Getter
//public class CreateAlertCommand {
//    private final String target; // ex) Slack의 경우 Channel
//    private final String body;   // 내용: JSON
//
//    private CreateAlertCommand(String target, String body) {
//        this.target = target;
//        this.body = body;
//    }
//
//    public static CreateAlertCommand create(String target, String body) {
//        return new CreateAlertCommand(target, body);
//    }
//}

