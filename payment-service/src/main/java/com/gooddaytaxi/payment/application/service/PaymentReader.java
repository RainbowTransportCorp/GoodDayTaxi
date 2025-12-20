package com.gooddaytaxi.payment.application.service;

import com.gooddaytaxi.payment.application.exception.PaymentErrorCode;
import com.gooddaytaxi.payment.application.exception.PaymentException;
import com.gooddaytaxi.payment.application.port.out.core.PaymentQueryPort;
import com.gooddaytaxi.payment.domain.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;


//paymentQueryPort.findById(paymentId)가 여러 서비스에서 중복되어 한 메서드로 정의하기 위한 클래스
@Component
@RequiredArgsConstructor
public class PaymentReader {
    private final PaymentQueryPort paymentQueryPort;

    public Payment getPayment (UUID paymentId) {
        return paymentQueryPort.findById(paymentId)
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));
    }
}
