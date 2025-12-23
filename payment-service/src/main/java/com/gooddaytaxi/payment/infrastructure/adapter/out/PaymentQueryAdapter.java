package com.gooddaytaxi.payment.infrastructure.adapter.out;

import com.gooddaytaxi.payment.application.port.out.core.PaymentQueryPort;
import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.entity.PaymentAttempt;
import com.gooddaytaxi.payment.domain.entity.Refund;
import com.gooddaytaxi.payment.domain.repository.PaymentIdentityView;
import com.gooddaytaxi.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentQueryAdapter implements PaymentQueryPort {
    private final PaymentRepository paymentRepository;

    @Override
    public Optional<Payment> findById(UUID paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public Optional<Payment> findByIdWithLock(UUID paymentId) {
        return paymentRepository.findByIdWithLock(paymentId);
    }

    @Override
    public Optional<PaymentAttempt> findLastAttemptByPaymentId(UUID paymentId) {
        return paymentRepository.findFirstByPaymentIdOrderByAttemptNoDesc(paymentId);
    }

    @Override
    public Page<Payment> searchPayments(String method, String status, UUID passeangerId, UUID driverId, UUID tripId, LocalDateTime startDay, LocalDateTime endDay, Pageable pageable) {
        return paymentRepository.searchPayments(method, status, passeangerId, driverId, tripId, startDay, endDay, pageable);
    }

    @Override
    public Map<UUID, PaymentAttempt> findLastAttemptsByPaymentIds(List<UUID> paymentsIds) {
        return paymentRepository.findLastAttemptsByPaymentIds(paymentsIds);
    }

    @Override
    public boolean existByTripIdAndNotStatusForCreate(UUID tripId) {
        return paymentRepository.existByTripIdAndNotStatusForCreate(tripId);
    }

    @Override
    public Optional<Payment> findByIdWithRefund(UUID paymentId) {
        return paymentRepository.findByIdWithRefund(paymentId);
    }


    @Override
    public Payment findLastByTripIdAndStatusForCreate(UUID tripId) {
        return paymentRepository.findLastByTripIdAndStatusForCreate(tripId);
    }

    @Override
    public Payment findLastByTripIdAndStatusForCreateWithLock(UUID tripId) {
        return paymentRepository.findLastByTripIdAndStatusForCreateWithLock(tripId);
    }

    @Override
    public Page<Refund> searchRefunds(String status, String reason, Boolean existRequest, UUID passeangerId, UUID driverId, UUID tripId, String method, Long minAmount, Long maxAmount, LocalDateTime startDay, LocalDateTime endDay, Pageable pageable) {
        return paymentRepository.searchRefunds(status, reason, existRequest, passeangerId, driverId, tripId, method, minAmount, maxAmount, startDay, endDay, pageable);
    }

    @Override
    public Optional<PaymentIdentityView> findIdentityViewById(UUID paymentId) {
        return paymentRepository.findIdentityById(paymentId);
    }

}
