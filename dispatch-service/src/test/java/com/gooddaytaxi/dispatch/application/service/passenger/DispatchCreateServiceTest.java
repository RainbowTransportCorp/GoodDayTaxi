package com.gooddaytaxi.dispatch.application.service.passenger;

import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchHistoryCommandPort;
import com.gooddaytaxi.dispatch.application.service.dispatch.DispatchDriverAssignmentService;
import com.gooddaytaxi.dispatch.application.usecase.create.DispatchCreateCommand;
import com.gooddaytaxi.dispatch.application.usecase.create.DispatchCreatePermissionValidator;
import com.gooddaytaxi.dispatch.application.usecase.create.DispatchCreateResult;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.gooddaytaxi.dispatch.application.exception.auth.UserRole.PASSENGER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DispatchCreateServiceTest {

    @Mock
    DispatchCommandPort dispatchCommandPort;

    @Mock
    DispatchHistoryCommandPort historyPort;

    @Mock
    DispatchDriverAssignmentService assignService;

    @Mock
    DispatchCreatePermissionValidator permissionValidator;

    @InjectMocks
    DispatchCreateService dispatchCreateService;

    @Test
    void 승객이_콜을_생성하면_REQUESTED_상태로_저장되고_기사배정이_호출된다() {
        // given
        UUID passengerId = UUID.randomUUID();

        DispatchCreateCommand command = DispatchCreateCommand.builder()
                .passengerId(passengerId)
                .pickupAddress("서울역")
                .destinationAddress("강남역")
                .role(PASSENGER)
                .build();

        Dispatch savedDispatch = Dispatch.create(
                passengerId,
                "서울역",
                "강남역"
        );

        when(dispatchCommandPort.save(any(Dispatch.class)))
                .thenReturn(savedDispatch);

        // when
        DispatchCreateResult result = dispatchCreateService.create(command);

        // then
        assertThat(result.getDispatchStatus()).isEqualTo(DispatchStatus.REQUESTED);
        assertThat(result.getPassengerId()).isEqualTo(passengerId);

        verify(permissionValidator).validate(PASSENGER);
        verify(dispatchCommandPort).save(any(Dispatch.class));
        verify(assignService).assignInitial(savedDispatch.getDispatchId());
        verify(historyPort).save(any());
    }
}
