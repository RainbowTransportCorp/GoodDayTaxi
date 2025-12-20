package com.gooddaytaxi.dispatch.application.service.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.usecase.timeout.AdminPermissionValidator;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminDispatchQueryServiceTest {

    @Mock
    DispatchQueryPort dispatchQueryPort;

    @Mock
    AdminPermissionValidator adminPermissionValidator;

    @InjectMocks
    AdminDispatchQueryService adminDispatchQueryService;

    @Test
    void 관리자가_배차_전체목록을_조회할_수_있다() {
        // given
        UUID passengerId = UUID.randomUUID();

        List<Dispatch> dispatches = List.of(
            Dispatch.create(
                passengerId,
                "서울역",
                "강남역"
            )
        );

        when(dispatchQueryPort.findAll())
            .thenReturn(dispatches);

        //when
        adminDispatchQueryService.getAllDispatches(UserRole.ADMIN);

        //then
        verify(adminPermissionValidator).validateAdminRead(UserRole.ADMIN);
        verify(dispatchQueryPort).findAll();
    }

    @Test
    void 관리자가_상태별_배차목록을_조회할_수_있다() {
        // given
        UUID passengerId = UUID.randomUUID();

        List<Dispatch> dispatches = List.of(
            Dispatch.create(
                passengerId,
                "서울역",
                "강남역"
            )
        );

        when(dispatchQueryPort.findByStatus(DispatchStatus.REQUESTED))
            .thenReturn(dispatches);

        //when
        adminDispatchQueryService.getDispatchesByStatus(UserRole.ADMIN,DispatchStatus.REQUESTED);

        //then
        verify(adminPermissionValidator).validateAdminRead(UserRole.ADMIN);
        verify(dispatchQueryPort).findByStatus(DispatchStatus.REQUESTED);
    }


    @Test
    void 관리자가_배차_상세조회를_할_수_있다() {
        // given
        UUID dispatchId = UUID.randomUUID();
        UUID passengerId = UUID.randomUUID();

        Dispatch dispatch = Dispatch.create(
            passengerId,
            "서울역",
            "강남역"
        );

        when(dispatchQueryPort.findById(dispatchId))
            .thenReturn(dispatch);

        // when
        adminDispatchQueryService.getDispatchDetail(UserRole.ADMIN, dispatchId);

        // then
        verify(adminPermissionValidator)
            .validateAdminRead(UserRole.ADMIN);

        verify(dispatchQueryPort)
            .findById(dispatchId);
    }
}