package com.gooddaytaxi.payment.application.event;

import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.entity.Refund;
import com.gooddaytaxi.payment.domain.enums.PaymentMethod;
import com.gooddaytaxi.payment.domain.enums.RefundReason;

import java.time.LocalDateTime;
import java.util.UUID;

public record RefundCompletedPayload(
        UUID notificationOriginId,
        UUID notifierId,   //이벤트 발동 유저 Id
        Long amount,
        String method,
        String pgProvider,
        UUID paymentId,
        UUID passeangerId,
        UUID driverId,
        UUID tripId,
        String reason,
        String message,
        LocalDateTime approvedAt,
        LocalDateTime refundedAt
) {
    public static RefundCompletedPayload from(Payment payment, Refund refund, UUID notifierId) {
        return new RefundCompletedPayload(
                refund.getId(),
                notifierId,
                payment.getAmount().value(),
                payment.getMethod().name(),
                payment.getMethod() == PaymentMethod.TOSS_PAY? payment.getAttempts().get(0).getPgProvider() : null,
                payment.getId(),
                payment.getPassengerId(),
                payment.getDriverId(),
                payment.getTripId(),
                refund.getReason().name(),
                createMessage(refund.getReason(), refund.getDetailReason()),
                payment.getApprovedAt(),
                refund.getRefundedAt()
        );

    }
    public static String createMessage(RefundReason reason, String detailReason) {
        String[] parts = detailReason.split("\\|", -1);
        String incidentAt = parts[0];
        String incidentSummary = parts[1];
        switch (reason) {
            case CUSTOMER_REQUEST -> {
                return String.format("""
                        %s경, 고객님께서 요청해주신 환불 신청에 따라
                        해당 결제 건에 대한 확인 절차가 진행되었습니다.
                        내부 확인 결과, 고객님의 요청 내용을 검토한 후
                        환불이 가능하다고 판단되어 고객 요청에 따른 환불을 진행하기로 결정하였습니다.
                        """, incidentAt);
            }
            case COMPANY_FAULT_SYSTEM -> {
                return String.format("""
                        %s경, 당사 결제 시스템 처리 과정에서 %s가 확인되었습니다.
                        내부 확인 결과, 본 문제는 고객님 또는 기사님의 과실이 아닌
                        당사 시스템 오류로 인한 문제로 판단되어,
                        고객 보호 차원에서 결제 취소 및 전액 환불을 진행하기로 결정하였습니다.
                        """, incidentAt,  incidentSummary);
            }
            case DUPLICATE_PAYMENT -> {
                return String.format("""
                        %s경, 결제 처리 과정에서 일시적인 중복 요청이 발생하여
                        동일한 결제가 두 차례 이상 처리된 것으로 확인되었습니다.
                        내부 확인 결과, 본 건은 고객님께서 의도하신 결제와 무관한
                        중복 결제 상황으로 판단되어,
                        고객 보호 차원에서 중복으로 결제된 금액에 대해 환불을 진행하기로 결정하였습니다.
                        """, incidentAt);
            }
            case PROMOTION_COMPENSATION -> {
                return String.format("""
                        %s경, 프로모션 적용 및 정산 과정에서
                        %s가 확인되었습니다.
                        내부 확인 결과, 해당 건에 대해 프로모션 적용 및 안내 과정 전반에서
                        고객님께 개별 보상이 필요한 사항이 확인되어,
                        고객 보호 및 보상 차원에서 프로모션 보상에 따른 환불을 진행하기로 결정하였습니다.
                        """, incidentAt,  incidentSummary);
            }
            case ADMIN_ADJUSTMENT -> {
                return String.format("""
                        %s경, 고객센터를 통한 문의 및 내부 검토 과정에서
                        해당 결제 건에 대해 추가 확인이 필요한 사항이 확인되었습니다.
                        내부 검토 결과, 서비스 이용 내역과 결제 내역을 종합적으로 확인한 후
                        관리자 판단에 따라 금액 조정이 필요하다고 판단되어,
                        고객 보호 차원에서 관리자 조정에 따른 환불을 진행하기로 결정하였습니다.
                        """, incidentAt);
            }
            default -> {
                return String.format("""
                        %s경, 해당 결제 건에 대해 내부 확인이 진행되었으며,
                        서비스 이용 및 결제 내역을 종합적으로 검토하는 과정에서
                        개별 안내가 필요한 사항이 확인되었습니다.
                        내부 검토 결과, 고객님께 불이익이 발생하지 않도록
                        종합적인 판단에 따라 기타 사유로 환불을 진행하기로 결정하였습니다.
                        """, incidentAt);
            }
        }
    }
}
