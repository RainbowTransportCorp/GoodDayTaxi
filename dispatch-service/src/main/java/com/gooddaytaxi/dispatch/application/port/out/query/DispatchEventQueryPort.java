package com.gooddaytaxi.dispatch.application.port.out.query;

import java.util.UUID;

public interface DispatchEventQueryPort {

    boolean existsTripCreated(UUID dispatchId);

}
