package com.gooddaytaxi.trip.application.validator;

import com.gooddaytaxi.trip.application.exception.TripErrorCode;
import com.gooddaytaxi.trip.application.exception.TripException;
import com.gooddaytaxi.trip.domain.model.enums.TripStatus;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TripValidator {
    /* =========================
       Role Check
       ========================= */
    public void checkRolePassenger(UserRole role) {
        if (role != UserRole.PASSENGER) {
            throw new TripException(TripErrorCode.PASSENGER_ROLE_REQUIRED);
        }
    }
    public void checkRoleDriver(UserRole role) {
        if (role != UserRole.DRIVER) {
            throw new TripException(TripErrorCode.DRIVER_ROLE_REQUIRED);
        }
    }
    public void checkRoleAdmin(UserRole role) {
        if (role != UserRole.ADMIN) {
            throw new TripException(TripErrorCode.ADMIN_ROLE_REQUIRED);
        }
    }
    public void checkRoleMasterAdmin(UserRole role) {
        if (role != UserRole.MASTER_ADMIN) {
            throw new TripException(TripErrorCode.MASTER_ADMIN_ROLE_REQUIRED);
        }
    }
    public void checkRoleAdminOrMaster(UserRole role) {
        if (!(role == UserRole.ADMIN || role == UserRole.MASTER_ADMIN)) {
            throw new TripException(TripErrorCode.ADMIN_OR_MASTER_REQUIRED);
        }
    }
    /** 승객 or 기사 둘 다 허용 */
    public void checkRolePassengerOrDriver(UserRole role) {
        if (!(role == UserRole.PASSENGER || role == UserRole.DRIVER)) {
            throw new TripException(TripErrorCode.TRIP_PERMISSION_DENIED);
        }
    }
    /* =========================
       Permission Check
       ========================= */
    public void checkPassengerPermission(UUID userId, UUID passengerId) {
        if (!passengerId.equals(userId)) {
            throw new TripException(TripErrorCode.TRIP_PERMISSION_DENIED);
        }
    }
    public void checkDriverPermission(UUID userId, UUID driverId) {
        if (!driverId.equals(userId)) {
            throw new TripException(TripErrorCode.TRIP_PERMISSION_DENIED);
        }
    }
    /** 운행 당사자(승객 or 기사)인지 확인 */
    public void checkTripActorPermission(
        UUID userId,
        UUID passengerId,
        UUID driverId
    ) {
        if (!(userId.equals(passengerId) || userId.equals(driverId))) {
            throw new TripException(TripErrorCode.TRIP_PERMISSION_DENIED);
        }
    }
    /* =========================
       Status Check
       ========================= */
    public void checkTripStatusStarted(TripStatus status) {
        if (status != TripStatus.STARTED) {
            throw new TripException(TripErrorCode.TRIP_STATUS_INVALID);
        }
    }
    public void checkTripStatusReady(TripStatus status) {
        if (status != TripStatus.READY) {
            throw new TripException(TripErrorCode.TRIP_STATUS_INVALID);
        }
    }
}