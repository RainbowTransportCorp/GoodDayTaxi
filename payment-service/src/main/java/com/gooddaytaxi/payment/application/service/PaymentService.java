package com.gooddaytaxi.payment.application.service;

import com.gooddaytaxi.payment.application.command.PaymentCreateCommand;
import com.gooddaytaxi.payment.application.port.out.PaymentCommandPort;
import com.gooddaytaxi.payment.application.result.PaymentCreateResult;
import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.vo.Fare;
import com.gooddaytaxi.payment.domain.vo.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentCommandPort paymentCommandPort;

    @Transactional
    public PaymentCreateResult createPayment(PaymentCreateCommand command) {
        //todo : 승객아이디, 운전자아이디, 탑승아이디 검증

        // 금액 검증
        Fare amount = Fare.of(command.getAmount());
        //결제 수단 검증
        PaymentMethod method = PaymentMethod.of(command.getMethod());

        //결제 청구서 생성
        Payment payment = new Payment(amount,  method, command.getPassengerId(), command.getDriverId(), command.getTripId());

        paymentCommandPort.save(payment);

        return new PaymentCreateResult(payment.getId(), payment.getMethod().name(), payment.getAmount().value(), payment.getPassengerId(), payment.getDriverId(), payment.getTripId());
    }
}
