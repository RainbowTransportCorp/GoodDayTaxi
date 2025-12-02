package com.gooddaytaxi.dispatch.domain.repository;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository //명시적 Repo
public interface DispatchRepository extends JpaRepository<Dispatch, UUID>, DispatchRepositoryCustom {
}

