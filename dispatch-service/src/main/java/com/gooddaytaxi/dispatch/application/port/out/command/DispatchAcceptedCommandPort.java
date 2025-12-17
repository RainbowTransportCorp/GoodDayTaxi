package com.gooddaytaxi.dispatch.application.port.out.command;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchAcceptedPayload;

public interface DispatchAcceptedCommandPort {

    /**
     * 배차가 수락되었을 때 발생하는 이벤트를 외부로 발행한다.
     * @param payload 수락된 배차정보가 담긴 payload
     */
    void publishAccepted(DispatchAcceptedPayload payload);
}
