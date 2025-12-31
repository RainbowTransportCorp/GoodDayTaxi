//package com.gooddaytaxi.dispatch.application.service.admin;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
//import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
//import com.gooddaytaxi.dispatch.application.port.out.command.DispatchTimeoutCommandPort;
//import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
//import com.gooddaytaxi.dispatch.application.service.dispatch.DispatchHistoryService;
//import com.gooddaytaxi.dispatch.application.usecase.timeout.AdminForceTimeoutCommand;
//import com.gooddaytaxi.dispatch.application.usecase.timeout.AdminPermissionValidator;
//import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
//import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
//import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
//import com.gooddaytaxi.dispatch.domain.model.enums.HistoryEventType;
//import java.util.UUID;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//class AdminDispatchServiceTest {
//
//    @Mock
//    DispatchQueryPort dispatchQueryPort;
//
//    @Mock
//    DispatchCommandPort dispatchCommandPort;
//
//    @Mock
//    DispatchTimeoutCommandPort dispatchTimeoutCommandPort;
//
//    @Mock
//    DispatchHistoryService dispatchHistoryService;
//
//    @Mock
//    AdminPermissionValidator adminPermissionValidator;
//
//    @InjectMocks
//    AdminDispatchService adminDispatchService;
//
//    @Test
//    void 마스터관리자가_강제타임아웃하면_배차상태가_TIMEOUT으로_변경된다() {
//        // given
//        UUID dispatchId = UUID.randomUUID();
//        UUID passengerId = UUID.randomUUID();
//        UUID masterId = UUID.randomUUID();
//
//        Dispatch dispatch = Dispatch.create(
//            passengerId,
//            "서울역",
//            "강남역"
//        );
//
//        when(dispatchQueryPort.findById(dispatchId))
//            .thenReturn(dispatch);
//
//        //when
//        adminDispatchService.forceTimeout(
//            UserRole.MASTER_ADMIN,
//            dispatchId,
//            AdminForceTimeoutCommand.builder()
//                .adminId(masterId)
//                .reason("관리자에 의한 강제 타임아웃")
//                .build()
//        );
//
//        //then
//        assertThat(dispatch.getDispatchStatus()).isEqualTo(DispatchStatus.TIMEOUT);
//
//        //상태 전이 저장
//        verify(dispatchCommandPort).save(dispatch);
//
//        //히스토리 기록 시도됨
//        verify(dispatchHistoryService).saveStatusChange(
//            eq(dispatchId),
//            eq(HistoryEventType.TIMEOUT),
//            eq(DispatchStatus.REQUESTED),
//            eq(DispatchStatus.TIMEOUT),
//            eq(ChangedBy.MASTER_ADMIN),
//            any()
//        );
//        //타임아웃 이벤트 발행 (사유는 히스토리에 존재)
//        verify(dispatchTimeoutCommandPort).publish(any());
//
//        //권한 체크
//        verify(adminPermissionValidator).validateMasterWrite(UserRole.MASTER_ADMIN);
//    }
//}