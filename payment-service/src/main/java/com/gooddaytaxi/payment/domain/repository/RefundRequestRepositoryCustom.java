package com.gooddaytaxi.payment.domain.repository;

import com.gooddaytaxi.payment.domain.entity.RefundRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface RefundRequestRepositoryCustom {

    Page<RefundRequest> searchRefundRequests(UUID paymentId, String status, String reasonKeyword, String method, UUID passeangerId, UUID driverId, LocalDateTime startDay, LocalDateTime endDay, Pageable pageable);
}
