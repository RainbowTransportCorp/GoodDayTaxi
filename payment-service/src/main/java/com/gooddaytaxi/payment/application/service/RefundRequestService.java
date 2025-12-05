package com.gooddaytaxi.payment.application.service;

import com.gooddaytaxi.payment.application.command.refundRequest.RefundRequestCreateCommand;
import com.gooddaytaxi.payment.application.exception.PaymentErrorCode;
import com.gooddaytaxi.payment.application.exception.PaymentException;
import com.gooddaytaxi.payment.application.port.out.PaymentQueryPort;
import com.gooddaytaxi.payment.application.port.out.RefundRequestCommandPort;
import com.gooddaytaxi.payment.application.port.out.RefundRequestQueryPort;
import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestCancelResult;
import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestCreateResult;
import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestReadResult;
import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.entity.RefundRequest;
import com.gooddaytaxi.payment.domain.enums.PaymentStatus;
import com.gooddaytaxi.payment.domain.enums.RefundRequestStatus;
import com.gooddaytaxi.payment.domain.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefundRequestService {
    private final RefundRequestCommandPort requestCommandPort;
    private final RefundRequestQueryPort requestQueryPort;
    private final PaymentQueryPort paymentQueryPort;

    @Transactional
    public RefundRequestCreateResult createRefundRequest(RefundRequestCreateCommand command, UUID userId, String role) {
        //환불 요청은 승객만 가능
        if(!UserRole.of(role).equals(UserRole.PASSENGER)) throw new PaymentException(PaymentErrorCode.PASSENGER_ROLE_REQUIRED);

        //해당 결제건이 존재하는지 확인
        Payment payment = paymentQueryPort.findById(command.paymentId()).orElseThrow(()-> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));
        //결제 상태 및 승객 일치 여부 확인
        if(payment.getStatus() != PaymentStatus.COMPLETED) throw new PaymentException(PaymentErrorCode.PAYMENT_STATUS_INVALID);
        if(!payment.getPassengerId().equals(userId)) throw new PaymentException(PaymentErrorCode.PAYMENT_PASSENGER_MISMATCH);

        //환불 생성
        RefundRequest request = new RefundRequest(command.paymentId(), command.reason());
        requestCommandPort.save(request);

        return new RefundRequestCreateResult(request.getId(), "환불 요청이 접수되었습니다.");
    }

    @Transactional(readOnly = true)
    public RefundRequestReadResult getRefundRequest(UUID requestId, UUID userId, String role) {
        RefundRequest request = requestQueryPort.findById(requestId).orElseThrow(()-> new PaymentException(PaymentErrorCode.REFUND_REQUEST_NOT_FOUND));
        Payment payment = paymentQueryPort.findById(request.getPaymentId()).orElseThrow(()-> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        //승객일 경우 본인 요청건만 조회 가능
        if(UserRole.of(role).equals(UserRole.PASSENGER)) {
            if (!payment.getPassengerId().equals(userId))
                throw new PaymentException(PaymentErrorCode.REFUND_REQUEST_NOT_PASSENGER);
        //기사일 경우 본인 요청건만 조회 가능
        }else if(UserRole.of(role).equals(UserRole.DRIVER)) {
            if (!payment.getDriverId().equals(userId))
                throw new PaymentException(PaymentErrorCode.REFUND_REQUEST_NOT_DRIVER);
        }
        return new RefundRequestReadResult(request.getId(), request.getPaymentId(), request.getReason(), request.getResponse(), request.getStatus());
    }

    @Transactional
    public RefundRequestCancelResult cancelRefundRequest(UUID requestId, UUID userId) {
        RefundRequest request = requestQueryPort.findById(requestId).orElseThrow(()-> new PaymentException(PaymentErrorCode.REFUND_REQUEST_NOT_FOUND));
        Payment payment = paymentQueryPort.findById(request.getPaymentId()).orElseThrow(()-> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));
        //해당 결제의 승객만 취소 가능
        if(!payment.getPassengerId().equals(userId)) throw new PaymentException(PaymentErrorCode.REFUND_REQUEST_NOT_PASSENGER);
        //환불 요청 상태가 REQUESTED 일때만 취소 가능
        if(request.getStatus() != RefundRequestStatus.REQUESTED) throw new PaymentException(PaymentErrorCode.PAYMENT_STATUS_INVALID);
        request.cancel();
        return new RefundRequestCancelResult(request.getId(), "환불 요청이 취소되었습니다.");
    }
}
