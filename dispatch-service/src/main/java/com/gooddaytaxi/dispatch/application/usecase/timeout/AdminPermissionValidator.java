package com.gooddaytaxi.dispatch.application.usecase.timeout;

import com.gooddaytaxi.dispatch.application.exception.DispatchPermissionDeniedException;
import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import org.springframework.stereotype.Component;

@Component
public class AdminPermissionValidator {

    /**
     * 조회 전용 (ADMIN, MASTER_ADMIN)
     */
    public void validateAdminRead(UserRole role) {
        if (role != UserRole.ADMIN && role != UserRole.MASTER_ADMIN) {
            throw new DispatchPermissionDeniedException(role);
        }
    }

    /**
     * 쓰기/개입 전용 (MASTER_ADMIN only)
     */
    public void validateMasterWrite(UserRole role) {
        if (role != UserRole.MASTER_ADMIN) {
            throw new DispatchPermissionDeniedException(role);
        }
    }
}
