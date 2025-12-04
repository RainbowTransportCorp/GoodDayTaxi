package com.gooddaytaxi.support.application.port.out.messaging;

import java.util.List;
import java.util.UUID;

public interface NotificationPushMessagingPort {
    void send(List<UUID> receiverIds, String title, String body);
}
