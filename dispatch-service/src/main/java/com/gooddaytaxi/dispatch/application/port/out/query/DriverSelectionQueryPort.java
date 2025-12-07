package com.gooddaytaxi.dispatch.application.port.out.query;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import java.util.UUID;

public interface DriverSelectionQueryPort {
    UUID selectCandidateDriver(Dispatch dispatch);
}
