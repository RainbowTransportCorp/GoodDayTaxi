package com.gooddaytaxi.payment.domain.entity;

import com.gooddaytaxi.payment.domain.vo.PaymentAttemptStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name="p_payment_attempts")
@NoArgsConstructor
public class PaymentAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="payment_attempt_id")
    private UUID id;

    @Column(nullable = false)
    private UUID IdempotencyKey;  //toss 결제에서 중복 방지를 위한 멱등성 키

    @Column(nullable = false)
    private String paymentKey;  //toss 결제에서 결제 고유 키

    @Column(nullable = false)
    private int attemptNo;  //결제 시도 횟수

    @Enumerated(EnumType.STRING)
    private PaymentAttemptStatus status;

    private LocalDateTime requestedAt; //toss 결제 요청 시간

    private LocalDateTime approvedAt;  //toss 결제 승인 시간

    private String pgMethod; //결제 승인된 결제 수단  //CARD, EASE_PAY, VIRTUAL_ACCOUNT

    private String pgProvider; //간편 결제시 결제 승인한 PG사  //토스페이, 카카오페이, 네이버페이 등

    private String pgFailReason; //결제 실패 사유

    private String failDetail; //서버에서 찾아낸 결제 실패 상세 사유

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="payment_id", nullable = false)
    private Payment payment;


    public PaymentAttempt(String paymentKey, UUID idempotencyKey, int attemptNo) {
        this.paymentKey = paymentKey;
        this.IdempotencyKey = idempotencyKey;
        this.attemptNo = attemptNo;
    }

    //tosspay 결제 승인시 정보 등록 및 성공 처리
    //간편결제시 구체적인 카드사는 토스페이밖에 없으므로 tosspay 승인 결제에만 사용
    public void registerApproveTosspay(LocalDateTime requestedAt, LocalDateTime approvedAt, String pgMethod, String provider) {
        this.status = PaymentAttemptStatus.SUCCESS;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.pgMethod = pgMethod;
        this.pgProvider = provider;
    }

    public void registerFailReason(String failReason) {
        this.pgFailReason = failReason;
    }
    public void registerFailReason(String failReason, String detailReason) {
        this.pgFailReason = failReason;
        this.failDetail = detailReason;
        this.status = PaymentAttemptStatus.FAILED;
    }

    public void registerPayment(Payment payment) {
        this.payment = payment;
    }
}
