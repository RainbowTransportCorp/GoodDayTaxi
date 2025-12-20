package com.gooddaytaxi.payment.application.validator;

import com.gooddaytaxi.payment.application.exception.PaymentErrorCode;
import com.gooddaytaxi.payment.application.exception.PaymentException;
import com.gooddaytaxi.payment.domain.enums.PaymentMethod;
import com.gooddaytaxi.payment.domain.enums.PaymentStatus;
import com.gooddaytaxi.payment.domain.enums.RefundRequestStatus;
import com.gooddaytaxi.payment.domain.enums.UserRole;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentValidator {

    //유저의 역할이 승객인지 확인
    public void checkRolePassenger(UserRole role) {
        if(role != UserRole.PASSENGER) {
            throw new PaymentException(PaymentErrorCode.PASSENGER_ROLE_REQUIRED);
        }
    }

    //해당 승객이 맞는지 확인
    public void checkPassengerPermission(UUID userId, UUID passangerId) {
        if(!passangerId.equals(userId))
            throw new PaymentException(PaymentErrorCode.PAYMENT_PASSENGER_MISMATCH);
    }

    //유저의 역할이 기사인지 확인
    public void checkRoleDriver(UserRole role) {
        if(role != UserRole.DRIVER) {
            throw new PaymentException(PaymentErrorCode.DRIVER_ROLE_REQUIRED);
        }
    }

    //해당 기사가 맞는지 확인
    public void checkDriverPermission(UUID userId, UUID driverId) {
        if(!driverId.equals(userId)) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_DRIVER_MISMATCH);
        }
    }
    //유저의 역할이 관리자인지 확인
    public void checkRoleAdmin(UserRole role) {
        if(role != UserRole.ADMIN) {
            throw new PaymentException(PaymentErrorCode.ADMIN_ROLE_REQUIRED);
        }
    }

    //유저의 역할이 마스터 관리자인지 확인
    public void checkRoleMasterAdmin(UserRole role) {
        if(role != UserRole.MASTER_ADMIN) {
            throw new PaymentException(PaymentErrorCode.MASTER_ADMIN_ROLE_REQUIRED);
        }
    }
    //유저의 역할이 승객이거나 기사인지 확인
    public void checkRolePassengerAndDriver(UserRole role) {
        if(!(role == UserRole.PASSENGER || role == UserRole.DRIVER)) {
            throw new PaymentException(PaymentErrorCode.PASSENGER_AND_DRIVER_ROLE_REQUIRED);
        }
    }
    //해당 승객이 맞는지 확인
    public void checkPassengerAndDriverPermission(UserRole role, UUID userId, UUID passangerId, UUID driverId) {
        if (role == UserRole.PASSENGER) checkPassengerPermission(userId, passangerId);
        else checkDriverPermission(userId, driverId);
    }
    //유저의 역할이 마스터 관리자거나 관리자인지 확인
    public void checkRoleAdminAndMaster(UserRole role) {
        if(!(role == UserRole.MASTER_ADMIN || role == UserRole.ADMIN)) {
            throw new PaymentException(PaymentErrorCode.MASTER_AND_ADMIN_ROLE_REQUIRED);
        }
    }
    //유저의 역할이 기사이거나 마스터 관리자인지 확인
    public void checkRoleDriverOrMaster(UserRole role) {
        if(!(role == UserRole.MASTER_ADMIN|| role == UserRole.DRIVER)) {
            throw new PaymentException(PaymentErrorCode.DRIVER_MASTER_ROLE_REQUIRED);
        }
    }

    //결제 수단이 토스페이인지 확인
    public void checkMethodTossPay(PaymentMethod method) {
        if(!(method == PaymentMethod.TOSS_PAY))
            throw new PaymentException(PaymentErrorCode.PAYMENT_METHOD_NOT_TOSSPAY);
    }

    //결제 수단이 실물결제인지 확인
    public void checkMethodPhysicalPayment(PaymentMethod method) {
        if(!(method == PaymentMethod.CARD || method == PaymentMethod.CASH)) {
            throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_METHOD);
        }
    }

    //결제 전 상태인지 확인
    public void checkStatusBeforePayment(PaymentStatus status) {
        if(!(status == PaymentStatus.PENDING || status == PaymentStatus.IN_PROCESS || status == PaymentStatus.FAILED))
            throw new PaymentException(PaymentErrorCode.PAYMENT_STATUS_INVALID);
    }

    //결제가 완료되었거나(환불 포함) 이미 취소된 결제는 불가
    public void checkStatusAfterPaymentOrCanceled(PaymentStatus status) {
        if(status == PaymentStatus.COMPLETED || status == PaymentStatus.REFUNDED || status == PaymentStatus.CANCELED)
            throw new PaymentException(PaymentErrorCode.PAYMENT_STATUS_INVALID);
    }

    //결제 상태가 대기인지 확인
    public void checkStatusPending(PaymentStatus status) {
        if(!status.equals(PaymentStatus.PENDING)) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_STATUS_INVALID);
        }
    }

    //결제가 완료된상태인지 확인
    public void checkPaymentStatusCompleted(PaymentStatus status) {
        if(!status.equals(PaymentStatus.COMPLETED)) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_STATUS_INVALID);
        }
    }

    //환불 요청 상태가 요청중인지 확인
    public void checkRefundRequestStatusRequested(RefundRequestStatus status) {
        if(!status.equals(RefundRequestStatus.REQUESTED)) {
            throw new PaymentException(PaymentErrorCode.REFUND_REQUEST_STATUS_INVALID);
        }
    }

    //해당 환불 요청이 승인된 상태인지 확인
    public void checkRefundRequestApproved(RefundRequestStatus status) {
        if(!status.equals(RefundRequestStatus.APPROVED)) {
            throw new PaymentException(PaymentErrorCode.REFUND_REQUEST_STATUS_INVALID);
        }
    }
}
