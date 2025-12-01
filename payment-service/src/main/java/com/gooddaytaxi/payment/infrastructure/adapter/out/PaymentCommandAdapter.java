package com.gooddaytaxi.payment.infrastructure.adapter.out;

import com.gooddaytaxi.payment.application.port.out.PaymentCommandPort;
import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCommandAdapter implements PaymentCommandPort {

    private final PaymentRepository paymentRepository;

    @Override
    public void save(Payment payment) {
        paymentRepository.save(payment);
    }
}
