package com.gooddaytaxi.payment.application.service;

import com.gooddaytaxi.common.core.exception.BusinessException;
import com.gooddaytaxi.common.core.exception.ErrorCode;
import com.gooddaytaxi.payment.application.command.RefundRequestCreateCommand;
import com.gooddaytaxi.payment.application.port.out.PaymentQueryPort;
import com.gooddaytaxi.payment.application.port.out.RefundRequestQueryPort;
import com.gooddaytaxi.payment.application.result.RefundRequestCreateResult;
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
        if(!UserRole.of(role).equals(UserRole.PASSENGER)) throw new BusinessException(ErrorCode.AUTH_FORBIDDEN_ROLE);

        //해당 결제건이 존재하는지와 환불 가능 상태인지 확인
        Payment payment = paymentQueryPort.findById(command.paymentId()).orElseThrow(()-> new BusinessException(ErrorCode.INVALID_INPUT_VALUE));
        if(payment.getStatus() != PaymentStatus.COMPLETED) throw new BusinessException(ErrorCode.INVALID_STATE);

        //환불 생성
        RefundRequest request = new RefundRequest(command.paymentId(), command.reason());
        RefundRequest saveRequest = requestQueryPort.save(request);

        return new RefundRequestCreateResult(saveRequest.getId(), "환불 요청이 접수되었습니다.");
    }
}
