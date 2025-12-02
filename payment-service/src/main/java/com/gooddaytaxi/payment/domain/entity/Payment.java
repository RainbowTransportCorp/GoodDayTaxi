package com.gooddaytaxi.payment.domain.entity;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.payment.domain.vo.Fare;
import com.gooddaytaxi.payment.domain.vo.PaymentMethod;
import com.gooddaytaxi.payment.domain.vo.PaymentStatus;
import com.gooddaytaxi.payment.infrastructure.converter.FareConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public Payment(Fare amount, PaymentMethod method, Long passengerId, Long driverId, UUID tripId) {
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.method = method;
        this.passengerId = passengerId;
        this.driverId = driverId;
        this.tripId = tripId;
    }

}
