package com.gooddaytaxi.payment.application.service;

import com.gooddaytaxi.payment.application.command.payment.PaymentTossPayCommand;
import com.gooddaytaxi.payment.application.exception.PaymentErrorCode;
import com.gooddaytaxi.payment.application.exception.PaymentException;
import com.gooddaytaxi.payment.application.message.SuccessMessage;
import com.gooddaytaxi.payment.application.port.out.core.ExternalPaymentPort;
import com.gooddaytaxi.payment.application.result.payment.ExternalPaymentConfirmResult;
import com.gooddaytaxi.payment.application.result.payment.PaymentApproveResult;
import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.entity.PaymentAttempt;
import com.gooddaytaxi.payment.domain.enums.PaymentMethod;
import com.gooddaytaxi.payment.domain.enums.PaymentStatus;
import com.gooddaytaxi.payment.domain.repository.PaymentRepository;
import com.gooddaytaxi.payment.domain.vo.Fare;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@SpringBootTest
class PaymentLockTimeoutIT {

    @Autowired PaymentRepository paymentRepo;   // 네 repository 이름으로 변경
    @Autowired PaymentService paymentService;         // approveTossPayment 있는 서비스로 변경
    @Autowired PlatformTransactionManager txManager;

    @MockitoBean
    ExternalPaymentPort externalPaymentPort; // 토스 호출 막기(테스트 안정화)

    private static final Logger log =
            LoggerFactory.getLogger(PaymentLockTimeoutIT.class);

    @Test
    void approve_success_should_complete_payment_and_return_success() {
        // given
        UUID tripId = UUID.randomUUID();
        UUID passengerId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();

        Fare fare = Fare.of(1000);
        Payment payment = new Payment(fare, PaymentMethod.TOSS_PAY, passengerId, driverId, tripId);
        payment.updateStatusToProcessing(); // IN_PROCESS

        paymentRepo.saveAndFlush(payment);

        String orderId = "TRIP__" + tripId;
        PaymentTossPayCommand cmd = new PaymentTossPayCommand("payKey", orderId, 1000L);

        when(externalPaymentPort.confirm(anyString(), any()))
                .thenReturn(new ExternalPaymentConfirmResult(
                        true,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        "TOSS_PAY",
                        "TOSS_PAY",
                        1000,
                        null
                ));

        // when
        PaymentApproveResult result =
                paymentService.approveTossPayment(cmd, passengerId, "PASSENGER", "idemKey-success-1");

        // then: 반환값
        assertThat(result).isNotNull();
        assertThat(result.paymentId()).isEqualTo(payment.getId());
        assertThat(result.message()).isEqualTo(SuccessMessage.PAYMENT_APPROVE_SUCCESS);

        // then: DB 반영 확인 (상태/attempt)
        Payment saved = paymentRepo.findById(payment.getId()).orElseThrow();
        assertThat(saved.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(saved.getApprovedAt()).isNotNull();
//        assertThat(saved.getAttempts()).isNotEmpty();          // 시도 기록이 추가됐는지
        // then: 마지막 결제 시도 확인 (LAZY 컬렉션 접근 X)
        PaymentAttempt lastAttempt =
                paymentRepo.findFirstByPaymentIdOrderByAttemptNoDesc(payment.getId())
                        .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_ATTEMPT_NOT_FOUND));

        assertThat(lastAttempt.getAttemptNo()).isEqualTo(1);
        assertThat(lastAttempt.getPaymentKey()).isEqualTo("payKey");
        assertThat(lastAttempt.getIdempotencyKey()).isEqualTo("idemKey-success-1");
    }

    @Test
    void approve_should_throw_my_jpa_lock_timeout_error_code() throws Exception {
        // given: IN_PROCESS Payment 준비
        UUID tripId = UUID.randomUUID();
        UUID passengerId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();

        // Fare 생성: 네 Fare 생성 방식에 맞춰 바꿔줘야 함
        Fare fare = Fare.of(1000); // <-- 만약 Fare.of(1000) 같은 팩토리면 그걸로 변경

        Payment payment = new Payment(fare, PaymentMethod.TOSS_PAY, passengerId, driverId, tripId);
        payment.updateStatusToProcessing(); // IN_PROCESS로 만들기

        paymentRepo.saveAndFlush(payment);

        // approve 커맨드 준비: orderId는 너 코드에서 substring(6) 후 UUID를 파싱하니까 prefix 6글자 맞춰줌
        String orderId = "TRIP__" + tripId; // "TRIP__"는 6글자
        PaymentTossPayCommand cmd = new PaymentTossPayCommand("payKey", orderId, 1000L);

        // 외부 결제 confirm은 호출되면 안 되거나, 호출되더라도 빠르게 성공하도록 mock
        when(externalPaymentPort.confirm(anyString(), any()))
                .thenReturn(new ExternalPaymentConfirmResult(
                        true,                       // success
                        LocalDateTime.now(),         // requestedAt
                        LocalDateTime.now(),         // approvedAt
                        "TOSS_PAY",                  // method
                        "TOSS_PAY",                  // provider
                        1000,                        // totalAmount
                        null                         // error (성공이므로 null)
                ));

        TransactionTemplate tt = new TransactionTemplate(txManager);

        CountDownLatch locked = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);

        // 락 홀더 스레드
        Thread holder = new Thread(() -> {
            log.info("[HOLDER] thread started");

            tt.execute(status -> {
                log.info("[HOLDER] tx begin");

                log.info("[HOLDER] try to acquire PESSIMISTIC_WRITE lock. tripId={}", tripId);
                Payment lockedPayment =
                        paymentRepo.findLastByTripIdAndStatusForCreateWithLock(tripId);

                log.info("[HOLDER] lock acquired. paymentId={}", lockedPayment.getId());
                locked.countDown();

                try {
                    log.info("[HOLDER] holding lock... waiting for release signal");
                    release.await();
                } catch (InterruptedException e) {
                    log.warn("[HOLDER] interrupted while holding lock", e);
                }

                log.info("[HOLDER] release signal received, tx end");
                return null;
            });

            log.info("[HOLDER] thread finished");
        });
        holder.start();
        log.info("[TEST] holder thread started");

// 락이 잡힐 때까지 기다림
        log.info("[TEST] waiting for holder to acquire lock");
        locked.await();
        log.info("[TEST] holder lock confirmed");

// when + then: 다른 트랜잭션에서 락을 잡으려다 timeout → 너의 PaymentException으로 변환되는지
        log.info("[TEST] invoking approveTossPayment (expect lock timeout)");

        assertThatThrownBy(() -> {
            log.info("[TEST] approveTossPayment call begin");
            paymentService.approveTossPayment(
                    cmd,
                    passengerId,
                    "PASSENGER",
                    "idemKey-1"
            );
            log.info("[TEST] approveTossPayment call end (UNEXPECTED)");
        })
                .isInstanceOf(PaymentException.class)
                .satisfies(ex -> {
                    PaymentException pe = (PaymentException) ex;
                    log.info("[TEST] PaymentException caught. errorCode={}", pe.getErrorCode());
                    assertThat(pe.getErrorCode())
                            .isEqualTo(PaymentErrorCode.PAYMENT_LOCK_TIMEOUT);
                });

// 락 해제
        log.info("[TEST] releasing holder lock");
        release.countDown();

        log.info("[TEST] waiting holder thread to finish");
        holder.join();

        log.info("[TEST] holder thread finished, test end");
    }
}

