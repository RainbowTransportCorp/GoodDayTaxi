package com.gooddaytaxi.support.application.port.in.account;

import java.util.UUID;

/* Account Domain과 통신하는 Port
*
*/
public interface AccountDomainCommunicationPort {
    // [GET] 외부 시스템 연동을 위한 Account 정보
    String getExternalInfo(UUID userId);
}
