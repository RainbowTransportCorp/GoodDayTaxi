package com.gooddaytaxi.dispatch.application.port.out.command;

import com.gooddaytaxi.dispatch.application.event.payload.TripCreateRequestPayload;

public interface TripCreateRequestCommandPort {

    /**
     * 운행을 시작하라는 명령형 이벤트를 외부로 발행
     * @param payload 시작될 운행정보가 담긴 payload
     */
    void publishTripCreateRequest (TripCreateRequestPayload payload);
}
