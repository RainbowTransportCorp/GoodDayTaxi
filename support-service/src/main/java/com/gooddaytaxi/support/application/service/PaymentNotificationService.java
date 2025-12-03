package com.gooddaytaxi.support.application.service;

import com.gooddaytaxi.support.application.port.in.payment.CompletePaymentUsecase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Payment 알림 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class PaymentNotificationService implements CompletePaymentUsecase {
}
