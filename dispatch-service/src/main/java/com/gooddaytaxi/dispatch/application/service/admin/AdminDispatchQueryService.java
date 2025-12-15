package com.gooddaytaxi.dispatch.application.service.admin;

import com.gooddaytaxi.dispatch.application.exception.DispatchPermissionDeniedException;
import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDispatchQueryService {

    private final DispatchQueryPort queryPort;

    public List<Dispatch> findAll(UserRole role, DispatchStatus status) {
        validateAdmin(role);

//        if (status == null) {
//            return queryPort.findAll();
//        }
//        return queryPort.findByStatus(status);
    }

    public Dispatch findDetail(UserRole role, UUID dispatchId) {
        validateAdmin(role);
        return queryPort.findById(dispatchId);
    }

    private void validateAdmin(UserRole role) {
        if (role != UserRole.ADMIN && role != UserRole.MASTER_ADMIN) {
            throw new DispatchPermissionDeniedException(role);
        }
    }
}
