package com.gooddaytaxi.payment.domain.repository;

import com.gooddaytaxi.payment.domain.entity.RefundRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RefundRequestRepository extends JpaRepository<RefundRequest, UUID>, RefundRequestRepositoryCustom {
}
