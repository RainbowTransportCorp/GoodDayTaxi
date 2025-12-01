package com.gooddaytaxi.payment.domain.entity;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.payment.domain.vo.Fare;
import com.gooddaytaxi.payment.domain.vo.PaymentMethod;
import com.gooddaytaxi.payment.domain.vo.PaymentStatus;
import com.gooddaytaxi.payment.infrastructure.converter.FareConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name="p_payments")
@NoArgsConstructor
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id")
    private UUID id;

    @Convert(converter = FareConverter.class)
    @Column(nullable = false)
    private Fare amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Column(nullable = false)
    private Long passengerId;

    @Column(nullable = false)
    private Long driverId;

    @Column(nullable = false)
    private UUID tripId;

    private UUID IdempotencyKey;  //toss 결제에서 중복 방지를 위한 멱등성 키

    private String paymentKey;  //toss 결제에서 결제 고유 키

    private LocalDateTime requestedAt; //toss 결제 요청 시간

    private LocalDateTime approvedAt;  //toss 결제 승인 시간

    private String pgMethod; //결제 승인된 결제 수단  //CARD, EASE_PAY, VIRTUAL_ACCOUNT

    private String pgProvider; //결제 승인된 PG사  //토스페이, 카카오페이, 네이버페이 등

    private String failReason; //결제 실패 사유


    public Payment(Fare amount, PaymentMethod method, Long passengerId, Long driverId, UUID tripId) {
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.method = method;
        this.passengerId = passengerId;
        this.driverId = driverId;
        this.tripId = tripId;
    }

    public void updateStatusToProcessing() {
        this.status = PaymentStatus.IN_PROCESS;
    }

    public void updateStatusToFailed() {this.status = PaymentStatus.FAILED;}

    public void registerIdentompencyKey(UUID idempotencyKey) {
        this.IdempotencyKey = idempotencyKey;
    }

    public void registerPaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }

    public void registerConfirmTosspay(LocalDateTime requestedAt, LocalDateTime approvedAt, String pgMethod) {
        this.status = PaymentStatus.COMPLETED;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.pgMethod = pgMethod;
    }

    public void registerFailReason(String failReason) {
        this.failReason = failReason;
    }

    public void registerProvider(String provider) {
        this.pgProvider = provider;
    }
}
