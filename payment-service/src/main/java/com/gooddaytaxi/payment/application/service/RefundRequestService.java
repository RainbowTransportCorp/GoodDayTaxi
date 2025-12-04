package com.gooddaytaxi.payment.application.service;

import com.gooddaytaxi.payment.application.command.refundRequest.RefundRequestCreateCommand;
import com.gooddaytaxi.payment.application.exception.PaymentErrorCode;
import com.gooddaytaxi.payment.application.exception.PaymentException;
import com.gooddaytaxi.payment.application.port.out.PaymentQueryPort;
import com.gooddaytaxi.payment.application.port.out.RefundRequestQueryPort;
import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestCreateResult;
import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.entity.RefundRequest;
import com.gooddaytaxi.payment.domain.enums.PaymentStatus;
import com.gooddaytaxi.payment.domain.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefundRequestService {
    private final RefundRequestQueryPort requestQueryPort;
    private final PaymentQueryPort paymentQueryPort;

    @Transactional
    public RefundRequestCreateResult createRefundRequest(RefundRequestCreateCommand command, String role) {
        //환불 요청은 승객만 가능
        if(!UserRole.of(role).equals(UserRole.PASSENGER)) throw new PaymentException(PaymentErrorCode.PASSENGER_ROLE_REQUIRED);

        //해당 결제건이 존재하는지와 환불 가능 상태인지 확인
        Payment payment = paymentQueryPort.findById(command.paymentId()).orElseThrow(()-> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));
        if(payment.getStatus() != PaymentStatus.COMPLETED) throw new PaymentException(PaymentErrorCode.PAYMENT_STATUS_INVALID);

        //환불 생성
        RefundRequest request = new RefundRequest(command.paymentId(), command.reason());
        RefundRequest saveRequest = requestQueryPort.save(request);

        return new RefundRequestCreateResult(saveRequest.getId(), "환불 요청이 접수되었습니다.");
    }
}
