package com.gooddaytaxi.support.application.port.out.internal.account;

import com.gooddaytaxi.support.adapter.out.internal.account.dto.DriverProfile;
import com.gooddaytaxi.support.adapter.out.internal.account.dto.UserProfile;

import java.util.List;
import java.util.UUID;

/* Account Domain과 통신하는 Port
*
*/
public interface AccountDomainCommunicationPort {
    UserProfile getUserInfo(UUID userId);
    DriverProfile getDriverInfo(UUID driverId);
    List<UUID> getMasterAdminUuids();
}
