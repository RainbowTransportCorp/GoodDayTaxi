package com.gooddaytaxi.dispatch.application.service.dispatch;

import com.gooddaytaxi.dispatch.application.port.out.command.DispatchAssignmentCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchRequestedCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.AccountDriverSelectionQueryPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.domain.model.enums.HistoryEventType;
import com.gooddaytaxi.dispatch.infrastructure.client.account.dto.DriverInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DispatchDriverAssignmentServiceTest {

    @Mock
    DispatchQueryPort dispatchQueryPort;

    @Mock
    DispatchCommandPort dispatchCommandPort;

    @Mock
    AccountDriverSelectionQueryPort driverSelectionQueryPort;

    @Mock
    DispatchAssignmentCommandPort assignmentCommandPort;

    @Mock
    DispatchHistoryService historyService;

    @Mock
    DispatchRequestedCommandPort requestedCommandPort;

    @InjectMocks
    DispatchDriverAssignmentService dispatchDriverAssignmentService;

    @Test
    void 최초_배차시도_시_ASSIGNING_상태로_변경되고_기사에게_요청을_전송한다() {
        // given
        UUID dispatchId = UUID.randomUUID();
        UUID passengerId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();

        Dispatch dispatch = Dispatch.create(
                passengerId,
                "서울역",
                "강남역"
        );

        when(dispatchQueryPort.findById(dispatchId))
                .thenReturn(dispatch);

        when(driverSelectionQueryPort.getAvailableDrivers(any()))
                .thenReturn(
                        new DriverInfo(
                                List.of(driverId),
                                "역삼동",
                                5
                        )
                );


        // when
        dispatchDriverAssignmentService.assignInitial(dispatchId);

        // then
        // 상태 전이 저장됨
        verify(dispatchCommandPort).save(dispatch);

        // 히스토리 기록 시도됨
        verify(historyService).saveStatusChange(
                eq(dispatchId),
                eq(HistoryEventType.STATUS_CHANGED),
                eq(DispatchStatus.REQUESTED),
                eq(DispatchStatus.ASSIGNING),
                eq(ChangedBy.SYSTEM),
                any()
        );

        // 기사에게 요청 이벤트 발행됨
        verify(requestedCommandPort).publishRequested(any());

        // 배차 로그 저장 시도됨
        verify(assignmentCommandPort).save(any());
    }


    @Test
    void ASSIGNING_상태에서_재배차를_요청하면_히스토리를_남기고_기사에게_이벤트를_전송한다() {
        // given
        UUID dispatchId = UUID.randomUUID();
        UUID passengerId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        int attemptNo = 2;

        Dispatch dispatch = Dispatch.create(
                passengerId,
                "서울역",
                "강남역"
        );

        // REQUESTED → ASSIGNING
        dispatch.startAssigning();

        when(dispatchQueryPort.findById(dispatchId))
                .thenReturn(dispatch);

        List<UUID> filteredDrivers = List.of(driverId);

        // when
        dispatchDriverAssignmentService.assignWithFilter(
                dispatchId,
                attemptNo,
                filteredDrivers
        );

        // then
        // 재배차 실행 히스토리 기록
        verify(historyService).saveStatusChange(
                eq(dispatchId),
                eq(HistoryEventType.REASSIGN_REQUESTED),
                eq(DispatchStatus.ASSIGNING),
                eq(DispatchStatus.ASSIGNING),
                eq(ChangedBy.SYSTEM),
                any()
        );

        // 기사에게 재배차 요청 이벤트 발행
        verify(requestedCommandPort).publishRequested(any());

        // 배차 로그 저장
        verify(assignmentCommandPort).save(any());
    }

    @Test
    void ASSIGNING이_아닌_상태에서는_아무_동작도_하지_않는다() {
        // given
        UUID dispatchId = UUID.randomUUID();
        UUID passengerId = UUID.randomUUID();

        Dispatch dispatch = Dispatch.create(
                passengerId,
                "서울역",
                "강남역"
        ); // REQUESTED 상태

        when(dispatchQueryPort.findById(dispatchId))
                .thenReturn(dispatch);

        // when
        dispatchDriverAssignmentService.assignWithFilter(
                dispatchId,
                1,
                List.of(UUID.randomUUID())
        );

        // then
        verifyNoInteractions(historyService);
        verifyNoInteractions(requestedCommandPort);
        verifyNoInteractions(assignmentCommandPort);
    }

    @Test
    void 재배차_대상_기사가_없으면_아무_동작도_하지_않는다() {

        // given
        UUID dispatchId = UUID.randomUUID();
        UUID passengerId = UUID.randomUUID();

        Dispatch dispatch = Dispatch.create(
                passengerId,
                "서울역",
                "강남역"
        );
        dispatch.startAssigning(); // ASSIGNING

        when(dispatchQueryPort.findById(dispatchId))
                .thenReturn(dispatch);

        // when
        dispatchDriverAssignmentService.assignWithFilter(
                dispatchId,
                2,
                List.of() // 빈 리스트
        );

        // then
        verifyNoInteractions(historyService);
        verifyNoInteractions(requestedCommandPort);
        verifyNoInteractions(assignmentCommandPort);
    }
}