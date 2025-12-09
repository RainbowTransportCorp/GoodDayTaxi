package com.gooddaytaxi.dispatch.domain.repository;

import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DispatchAssignmentRepository extends JpaRepository<DispatchAssignmentLog, UUID>, DispatchAssignmentRepositoryCustom {

}
