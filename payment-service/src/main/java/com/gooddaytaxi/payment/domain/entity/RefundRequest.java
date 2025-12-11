package com.gooddaytaxi.payment.domain.entity;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.payment.domain.enums.RefundRequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    @Column(nullable = false)
    private LocalDateTime requestedAt;  //요청시각

    private LocalDateTime approvedAt;  //승인시각
    private LocalDateTime rejectedAt;  //기각시각



    private String reason;  //요청 내용

    private String response;  //응답 내용

    public RefundRequest (UUID paymentId, String reason) {
        this.paymentId = paymentId;
        this.status = RefundRequestStatus.REQUESTED;
        this.requestedAt = LocalDateTime.now();
        this.reason = reason;
    }


    public void respond(Boolean approve, String response) {
        this.response = response;
        if(approve) {
            this.status = RefundRequestStatus.APPROVED;
            this.approvedAt = LocalDateTime.now();
        }else {
            this.status = RefundRequestStatus.REJECTED;
            this.rejectedAt = LocalDateTime.now();
        }
    }
    public void cancel() {
        this.status = RefundRequestStatus.CANCELED;
    }
}
