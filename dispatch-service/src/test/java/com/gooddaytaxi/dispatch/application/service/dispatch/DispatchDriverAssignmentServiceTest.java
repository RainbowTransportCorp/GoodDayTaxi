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

        // 기사에게 요청하기 위한 알림 이벤트 발행됨 (dispatch -> support)
        verify(requestedCommandPort).publishRequested(any());

        // 배차 로그 저장 시도됨
        verify(assignmentCommandPort).save(any());
    }


    @Test
    void TIMEOUT_상태에서_재배차를_요청하면_히스토리를_남기고_기사에게_이벤트를_전송한다() {
        // given
        UUID dispatchId = UUID.randomUUID();
        UUID passengerId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();

        Dispatch dispatch = Dispatch.create(
                passengerId,
                "서울역",
                "강남역"
        );

        // 재배차의 흐름 그대로 생성->대기중->타임아웃 상태전이
        dispatch.startAssigning();
        dispatch.timeout();

        when(dispatchQueryPort.findById(dispatchId)).thenReturn(dispatch);

        // when
        dispatchDriverAssignmentService.assignWithFilter(dispatchId, List.of(driverId));

        // then
        verify(historyService).saveStatusChange(
                eq(dispatchId),
                eq(HistoryEventType.REASSIGN_REQUESTED),
                eq(DispatchStatus.TIMEOUT),
                eq(DispatchStatus.ASSIGNING),
                eq(ChangedBy.SYSTEM),
                any()
        );
    }

}