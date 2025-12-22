package com.gooddaytaxi.payment.domain.entity;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.payment.domain.enums.RefundReason;
import com.gooddaytaxi.payment.domain.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name="p_refunds")
@NoArgsConstructor
public class Refund extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "refund_id")
    private UUID id;

    @Column(nullable = false, length=10)
    @Enumerated(EnumType.STRING)
    private RefundStatus status;

    @Column(nullable = false, length=30)
    @Enumerated(EnumType.STRING)
    private RefundReason reason;

    private String detailReason;


    private UUID requestId;  //환불 요청 ID

    private LocalDateTime refundedAt; //서버 내부의 환불 완료 시간
    
    private LocalDateTime executedAt;  //현금/카드 같은 실물 환불이 집행된 시간

    private LocalDateTime canceledAt; //토스페이에서 환불 승인 시간

    private String transactionKey; //토스페이 환불 거래 고유 키
    private String idempotencyKey;  //토스페이 환불 멱등성 키

    private String pgFailReason; //토스페이 환불 실패 사유

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    public Refund(RefundReason reason, String detailReason, UUID requestId) {
        this.reason = reason;
        this.detailReason = detailReason;
        if(requestId!=null) this.requestId = requestId;
    }
    public Refund(RefundReason reason, String detailReason, UUID requestId, String idempotencyKey) {
        this.reason = reason;
        this.detailReason = detailReason;
        if(requestId!=null) this.requestId = requestId;
        this.idempotencyKey = idempotencyKey;
    }

    public void registerPayment(Payment payment) {
        this.payment = payment;
    }

    public void success(LocalDateTime canceledAt, String transactionKey) {
        this.status = RefundStatus.SUCCESS;
        this.canceledAt = canceledAt;
        this.transactionKey = transactionKey;
        this.refundedAt = LocalDateTime.now();
    }

    public void markExecuted(LocalDateTime executedAt) {
        this.executedAt = executedAt;
        this.refundedAt = LocalDateTime.now();
        this.status = RefundStatus.SUCCESS;
    }

    public void registerFailReason(String failReason) {
        this.status = RefundStatus.FAILED;
        this.pgFailReason = failReason;
    }
}
