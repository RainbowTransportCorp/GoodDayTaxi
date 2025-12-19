package com.gooddaytaxi.dispatch.application.service.passenger;


import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.query.DispatchSummaryResult;
import com.gooddaytaxi.dispatch.application.usecase.query.PassengerQueryPermissionValidator;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static com.gooddaytaxi.dispatch.application.exception.auth.UserRole.PASSENGER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PassengerDispatchQueryServiceTest {

    @Mock
    PassengerQueryPermissionValidator permissionValidator;

    @Mock
    DispatchQueryPort dispatchQueryPort;

    @InjectMocks
    PassengerDispatchQueryService dispatchQueryService;

    @Test
    void 승객_권한으로_자신의_배차_목록을_조회한다() {
        //given
        UUID passengerId = UUID.randomUUID();
        List<Dispatch> dispatches = List.of(
                Dispatch.create(
                        passengerId,
                        "서울역",
                        "강남역"
                )
        );

        when(dispatchQueryPort.findAllByPassengerId(passengerId))
                .thenReturn(dispatches);


        //when
        List<DispatchSummaryResult> result = dispatchQueryService.getDispatchList(passengerId, PASSENGER);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDispatchId())
                .isEqualTo(dispatches.get(0).getDispatchId());

        verify(permissionValidator).validate(PASSENGER);
        verify(dispatchQueryPort).findAllByPassengerId(passengerId);

    }

    @Test
    void TIMEOUT_된_배차는_목록_조회에서_제외된다() {
        // given
        UUID passengerId = UUID.randomUUID();

        Dispatch normal = Dispatch.create(passengerId, "서울역", "강남역");

        Dispatch timeout = Dispatch.create(passengerId, "서울역", "홍대입구");

        timeout.startAssigning(); //배차 중으로 상태전이
        timeout.timeout(); // 타임아웃으로 상태 전이

        when(dispatchQueryPort.findAllByPassengerId(passengerId))
                .thenReturn(List.of(normal, timeout));

        // when
        List<DispatchSummaryResult> result =
                dispatchQueryService.getDispatchList(passengerId, PASSENGER);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDispatchId())
                .isEqualTo(normal.getDispatchId());
    }


    @Test
    void getDispatchDetail() {
    }
}