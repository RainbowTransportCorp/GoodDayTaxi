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

    private LocalDateTime canceledAt; //환불 승인 시간

    private String transactionKey; //환불 거래 고유 키

    private String pgFailReason; //환불 실패 사유


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    public Refund(RefundReason reason, String detailReason, UUID requestId) {
        this.reason = reason;
        this.detailReason = detailReason;
        if(requestId!=null) this.requestId = requestId;
    }

    public void registerPayment(Payment payment) {
        this.payment = payment;
    }

    public void success(LocalDateTime canceledAt, String transactionKey) {
        this.status = RefundStatus.SUCCESS;
        this.canceledAt = canceledAt;
        this.transactionKey = transactionKey;
    }

    public void registerFailReason(String failReason) {
        this.status = RefundStatus.FAILED;
        this.pgFailReason = failReason;
    }
}
