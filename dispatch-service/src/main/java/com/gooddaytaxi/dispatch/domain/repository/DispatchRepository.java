package com.gooddaytaxi.dispatch.domain.repository;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DispatchRepository extends JpaRepository<Dispatch, UUID>, DispatchRepositoryCustom {
}

