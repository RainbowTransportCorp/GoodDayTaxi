package com.gooddaytaxi.payment.domain.entity;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.payment.domain.enums.RefundRequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(name="p_refund_requests")
@NoArgsConstructor
public class RefundRequest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "request_id")
    private UUID id;

    @Column(nullable = false)
    private UUID paymentId;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RefundRequestStatus status;

    private String reason;  //요청 내용

    private String response;  //응답 내용

    public RefundRequest (UUID paymentId, String reason) {
        this.paymentId = paymentId;
        this.status = RefundRequestStatus.REQUESTED;
        this.reason = reason;
    }


    public void respond(Boolean approve, String response) {
        this.response = response;
        if(approve) {
            this.status = RefundRequestStatus.APPROVED;
        }else {
            this.status = RefundRequestStatus.REJECTED;
        }
    }
}
