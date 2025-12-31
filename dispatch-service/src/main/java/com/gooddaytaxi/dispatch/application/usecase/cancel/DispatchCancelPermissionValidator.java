package com.gooddaytaxi.dispatch.application.usecase.cancel;

import com.gooddaytaxi.dispatch.application.exception.DispatchPermissionDeniedException;
import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import com.gooddaytaxi.dispatch.application.exception.auth.DispatchNotOwnerPassengerException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DispatchCancelPermissionValidator {

    /**
     * 배차를 취소할 때 권한 체크
     * @param role 요청자의 권한
     * @param requestPassengerId 요청한 승객의 식별자
     * @param ownerPassengerId 배차를 생성한 승객 정보
     */
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

