package com.gooddaytaxi.dispatch.domain.model.enums;

//외부에게 보내는 '알림'에 사용될 이벤트 타입 ('명령'에 해당하는 커멘드와 별도입니다.)
public enum DispatchExternalEventType {
    DISPATCH_REQUESTED, DISPATCH_CONFIRMED
}
