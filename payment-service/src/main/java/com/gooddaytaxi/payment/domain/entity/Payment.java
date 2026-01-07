package com.gooddaytaxi.payment.domain.entity;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.payment.domain.vo.Fare;
import com.gooddaytaxi.payment.domain.enums.PaymentMethod;
import com.gooddaytaxi.payment.domain.enums.PaymentStatus;
import com.gooddaytaxi.payment.infrastructure.converter.FareConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(nullable = false, length=20)
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Column(nullable = false)
    private UUID passengerId;

    @Column(nullable = false)
    private UUID driverId;

    @Column(nullable = false)
    private UUID tripId;

    private LocalDateTime approvedAt;  //서버 내 승인 시간

    @Column(length = 200)
    private String cancelReason;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    @OrderBy("attemptNo DESC")
    private List<PaymentAttempt> attempts = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "payment")
    private Refund refund;




    public Payment(Fare amount, PaymentMethod method, UUID passengerId, UUID driverId, UUID tripId) {
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.method = method;
        this.passengerId = passengerId;
        this.driverId = driverId;
        this.tripId = tripId;
    }

    //진행 상태로 변경
    public void updateStatusToProcessing() {
        this.status = PaymentStatus.IN_PROCESS;
    }

    //결제 청구서의 최종 완료 상태
    public void updateStatusToComplete() {
        this.status = PaymentStatus.COMPLETED;
        this.approvedAt = LocalDateTime.now();
    }

    //결제 청구서의 현재 실패 상태
    public void updateStatusToFailed() {this.status = PaymentStatus.FAILED;}

    public void addAttempt (PaymentAttempt attempt) {
        this.attempts.add(attempt);
        attempt.registerPayment(this);
    }

    public void registerRefund (Refund refund, boolean isSuccess) {
        if(isSuccess)this.status = PaymentStatus.REFUNDED;
        this.refund = refund;
        refund.registerPayment(this);
    }

    public void cancelPayment(String cancelReason) {
        this.status = PaymentStatus.CANCELED;
        this.cancelReason = cancelReason;
    }

    public void changeAmount(Fare amount) {
        this.status = PaymentStatus.PENDING;
        this.amount = amount;
    }

    public void changeMethod(PaymentMethod method) {
        this.status = PaymentStatus.PENDING;
        this.method = method;
    }
}
