package com.gooddaytaxi.dispatch.application.usecase.cancel;

import com.gooddaytaxi.dispatch.application.exception.DispatchPermissionDeniedException;
import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import com.gooddaytaxi.dispatch.domain.exception.DispatchNotOwnerPassengerException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DispatchCancelPermissionValidator {

    public void validate(
            UserRole role,
            UUID requestPassengerId,
            UUID ownerPassengerId
    ) {

        // Cancel은 승객만 가능 (마스터는 별도 UseCase 권장)
        if (role != UserRole.PASSENGER) {
            throw new DispatchPermissionDeniedException(role);
        }

        // 해당 Dispatch를 생성한 승객인지 검증
        if (!requestPassengerId.equals(ownerPassengerId)) {
            throw new DispatchNotOwnerPassengerException();
        }
    }
}

