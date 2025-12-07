package com.gooddaytaxi.payment.application.service;

import com.gooddaytaxi.payment.application.command.refund.ExternalPaymentCancelCommand;
import com.gooddaytaxi.payment.application.command.refund.RefundCreateCommand;
import com.gooddaytaxi.payment.application.exception.PaymentErrorCode;
import com.gooddaytaxi.payment.application.exception.PaymentException;
import com.gooddaytaxi.payment.application.port.out.ExternalPaymentPort;
import com.gooddaytaxi.payment.application.port.out.PaymentCommandPort;
import com.gooddaytaxi.payment.application.port.out.PaymentQueryPort;
import com.gooddaytaxi.payment.application.port.out.RefundRequestQueryPort;
import com.gooddaytaxi.payment.application.result.payment.ExternalPaymentConfirmResult;
import com.gooddaytaxi.payment.application.result.refund.ExternalPaymentCancelResult;
import com.gooddaytaxi.payment.application.result.refund.RefundCreateResult;
import com.gooddaytaxi.payment.application.validator.PaymentValidator;
import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.entity.Refund;
import com.gooddaytaxi.payment.domain.entity.RefundRequest;
import com.gooddaytaxi.payment.domain.enums.RefundReason;
import com.gooddaytaxi.payment.domain.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundService {

    private final PaymentCommandPort paymentCommandPort;
    private final PaymentQueryPort paymentQueryPort;
    private final ExternalPaymentPort externalPaymentPort;
    private final RefundRequestQueryPort requestQueryPort;
    private final PaymentFailureRecorder failureRecorder;
    private final PaymentValidator validator;

    @Transactional
    public RefundCreateResult confirmTosspayRefund(UUID paymentId, RefundCreateCommand command, String role) {
        //해당 결제가 있는지 확인
        Payment payment = paymentQueryPort.findById(paymentId).orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        //롤이 관리자인지 확인
        validator.checkRoleAdmin(UserRole.of(role));
        //결제 수단이 토스페이인지 확인
        validator.checkMethodTossPay(payment.getMethod());
        //결제상태가 완료 상태인지 확인
        validator.checkPaymentStatusCompleted(payment.getStatus());
        //환불 요청이 있다면 해당 요청이 승인된 상태인지 확인
        if(command.requestId() != null) {
            RefundRequest request = requestQueryPort.findById(command.requestId()).orElseThrow(() -> new PaymentException(PaymentErrorCode.REFUND_REQUEST_NOT_FOUND));
            validator.checkRefundRequestApproved(request.getStatus());
        }

        //해당 결제으 마지막 시도의 pamentKey 가져오기
        String paymentKey = payment.getAttempts().get(0).getPaymentKey();
        //멱등성 키 생성
        UUID idempotencyKey = UUID.randomUUID();
        //환불 사유 매핑
        RefundReason reason = RefundReason.of(command.reason());

        Refund refund = new Refund(
                reason,
                command.detailReason(),
                command.requestId()
        );
        //토스에 환불 요청
        ExternalPaymentCancelResult result = externalPaymentPort.cancelTosspayPayment(paymentKey, idempotencyKey, new ExternalPaymentCancelCommand(reason.getDescription()));

        //실패시 실패 기록 및 예외 던지기
        if(!result.success()) {
            // 실패 기록은 별도 트랜잭션으로 먼저 확정
            failureRecorder.recordCancelFailure(payment, refund, result.error());

            throw new PaymentException(PaymentErrorCode.TOSSPAY_CANCEL_FAILED);

        }

        //만약 환불 금액과 결제 금액이 다르면 에러 로그 남기기
        if(!(payment.getAmount().value()==result.cancelAmount()))
            log.error("TossPay cancel amount mismatch for paymentId={}: expected={}, actual={}",
                    payment.getId(), payment.getAmount().value(), result.cancelAmount());

//        성공시 결제 엔티티에 저장
        refund.success(result.canceledAt(), result.transactionKey());
        payment.registerRefund(refund, true);

        paymentCommandPort.save(payment);

        return new RefundCreateResult(refund.getId(), "환불이 완료되었습니다!");
    }

    //디버그용 - 토스페이 외부결제 정보 조회
    public String getExternalTossPay(UUID paymentId) {
        Payment payment = paymentQueryPort.findById(paymentId).orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        String paymentKey = payment.getAttempts().get(0).getPaymentKey();
        log.info("DEBUG local cancel paymentKey={}", paymentKey);

        ExternalPaymentConfirmResult dto = externalPaymentPort.getPayment(paymentKey);
        return dto.toString();
    }
}
