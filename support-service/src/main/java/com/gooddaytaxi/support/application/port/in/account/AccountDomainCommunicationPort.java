package com.gooddaytaxi.support.application.port.in.account;

import com.gooddaytaxi.support.adapter.out.external.http.dto.UserInfo;

import java.util.UUID;

/* Account Domain과 통신하는 Port
*
*/
public interface AccountDomainCommunicationPort {
    UserInfo getUserInfo(UUID userId);
}
