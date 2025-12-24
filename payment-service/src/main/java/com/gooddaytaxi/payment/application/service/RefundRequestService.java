package com.gooddaytaxi.payment.application.service;

import com.gooddaytaxi.payment.application.command.refundRequest.RefundReqeustSearchCommand;
import com.gooddaytaxi.payment.application.command.refundRequest.RefundRequestCreateCommand;
import com.gooddaytaxi.payment.application.command.refundRequest.RefundRequestResponseCreateCommand;
import com.gooddaytaxi.payment.application.event.payload.RefundRequestCreatePayload;
import com.gooddaytaxi.payment.application.event.payload.RefundRequestRejectedPayload;
import com.gooddaytaxi.payment.application.exception.PaymentErrorCode;
import com.gooddaytaxi.payment.application.exception.PaymentException;
import com.gooddaytaxi.payment.application.message.SuccessMessage;
import com.gooddaytaxi.payment.application.port.out.core.PaymentQueryPort;
import com.gooddaytaxi.payment.application.port.out.core.RefundRequestCommandPort;
import com.gooddaytaxi.payment.application.port.out.core.RefundRequestQueryPort;
import com.gooddaytaxi.payment.application.port.out.event.PaymentEventCommandPort;
import com.gooddaytaxi.payment.application.result.payment.PaymentAdminReadResult;
import com.gooddaytaxi.payment.application.result.refund.PgRefundResult;
import com.gooddaytaxi.payment.application.result.refund.RefundAdminReadResult;
import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestAdminReadResult;
import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestCancelResult;
import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestCreateResult;
import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestReadResult;
import com.gooddaytaxi.payment.application.validator.PaymentValidator;
import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.entity.Refund;
import com.gooddaytaxi.payment.domain.entity.RefundRequest;
import com.gooddaytaxi.payment.domain.enums.PaymentStatus;
import com.gooddaytaxi.payment.domain.enums.UserRole;
import com.gooddaytaxi.payment.domain.repository.PaymentIdentityView;
import com.gooddaytaxi.payment.domain.vo.RefundRequestSortBy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefundRequestService {
    private final RefundRequestCommandPort requestCommandPort;
    private final RefundRequestQueryPort requestQueryPort;
    private final PaymentQueryPort paymentQueryPort;
    private final PaymentEventCommandPort eventCommandPort;
    private final PaymentReader paymentReader;
    private final PaymentValidator validator;

    @Transactional
    public RefundRequestCreateResult createRefundRequest(RefundRequestCreateCommand command, UUID userId, String role) {
        //환불 요청은 승객만 가능
        validator.checkRolePassenger(UserRole.of(role));
        //해당 결제건이 존재하는지 확인
        Payment payment = paymentReader.getPayment(command.paymentId());
        //결제 상태 및 승객 일치 여부 확인
        validator.checkPaymentStatusCompleted(payment.getStatus());
        validator.checkPassengerPermission(userId, payment.getPassengerId());

        //환불 생성
        RefundRequest request = new RefundRequest(command.paymentId(), command.reason());
        requestCommandPort.save(request);

        //이벤트 발행
        eventCommandPort.publishRefundRequestCreated(RefundRequestCreatePayload.from(payment, request));

        return new RefundRequestCreateResult(request.getId(), SuccessMessage.REQUEST_CREATE_SUUCCESS);
    }

    //환불 요청 단건 조회 - 승객만 가능
    @Transactional(readOnly = true)
    public RefundRequestReadResult getRefundRequest(UUID requestId, UUID userId, String role) {
        validator.checkRolePassenger(UserRole.of(role));

        RefundRequest request = getRequest(requestId);
        PaymentIdentityView payment = paymentQueryPort.findIdentityViewById(request.getPaymentId()).orElseThrow(()-> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        //본인 요청건만 조회 가능
        validator.checkPassengerPermission(userId, payment.getPassengerId());

        return new RefundRequestReadResult(request.getId(), request.getPaymentId(), request.getReason(), request.getResponse(), request.getStatus());
    }

    @Transactional(readOnly = true)
    public RefundRequestAdminReadResult getAdminRefundRequest(UUID requestId, String role) {
        //관리자인지 체크
        validator.checkRoleAdminAndMaster(UserRole.of(role));
        //환불 요청정보 가져오기
        RefundRequest request = getRequest(requestId);
        Payment payment = paymentReader.getPayment(request.getPaymentId());
        //결제 추가 정보 가져오기
        PaymentAdminReadResult paymentResult = getPaymentAdminReadResult(payment);
        RefundAdminReadResult refundResult =
                payment.getStatus() == PaymentStatus.REFUNDED? getRefundReadResult(payment.getRefund()): null;

        //응답 반환
        return new RefundRequestAdminReadResult(
                request.getId(),
                request.getReason(),
                request.getResponse(),
                request.getStatus(),
                paymentResult,
                refundResult
        );
    }



    //환불 요청 검색 - 승객만 가능
    @Transactional(readOnly = true)
    public Page<RefundRequestReadResult> searchRefundRequests(RefundReqeustSearchCommand command, UUID userId, String role) {
        UserRole userRole = UserRole.of(role);
        validator.checkRolePassenger(userRole);

        //정렬 조건 체크
        RefundRequestSortBy.checkValid(command.sortBy());

        //pageable 생성
        Pageable pageable = CommonService.toPageable(command.sortAscending(),command.page(), command.size(), command.sortBy());

        Page<RefundRequest> requests = requestQueryPort.searchRefundRequests(
                null,
                command.status(),
                command.reasonKeyword(),
                null,
                userId,
                 null,
                command.startDay(),
                command.endDay(),
                pageable);
        return requests.map(request -> new RefundRequestReadResult(request.getId(), request.getPaymentId(), request.getReason(), request.getResponse(), request.getStatus()));
    }

    //관리자용 환불 요청 검색 - 관리자용
    @Transactional(readOnly = true)
    public Page<RefundRequestReadResult> searchAdminRefundRequests(RefundReqeustSearchCommand command, String role) {
        //권한 체크
        validator.checkRoleAdminAndMaster(UserRole.of(role));
        //정렬 조건 체크
        RefundRequestSortBy.checkValid(command.sortBy());
        //pageable 생성
        Pageable pageable = CommonService.toPageable(command.sortAscending(),command.page(), command.size(), command.sortBy());

        Page<RefundRequest> requests = requestQueryPort.searchRefundRequests(
                command.paymentId(),
                command.status(),
                command.reasonKeyword(),
                command.method(),
                command.passengerId(),
                command.driverId(),
                command.startDay(),
                command.endDay(),
                pageable);
        return requests.map(request -> new RefundRequestReadResult(request.getId(), request.getPaymentId(), request.getReason(), request.getResponse(), request.getStatus()));
    }

    @Transactional
    public RefundRequestCreateResult respondToRefundRequest(RefundRequestResponseCreateCommand command, UUID userId, String role) {
        //환불 요청 응답은 최고관리지만 가능
        validator.checkRoleMasterAdmin(UserRole.of(role));

        RefundRequest request = getRequest(command.requestId());
        validator.checkRefundRequestStatusRequested(request.getStatus());
        request.respond(command.approve(), command.response());
        if(!command.approve()) {
            //기각시 이벤트 발행
            PaymentIdentityView payment = paymentQueryPort.findIdentityViewById(request.getPaymentId()).orElseThrow(()-> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));
            eventCommandPort.publishRefundRequestRejected(RefundRequestRejectedPayload.from(payment, request, userId));
        }

        return new RefundRequestCreateResult(request.getId(), SuccessMessage.REQUEST_RESPONSE_SUCCESS);
    }

    @Transactional
    public RefundRequestCancelResult cancelRefundRequest(UUID requestId, UUID userId, String role) {
        //환불 요청 취소는 승객만 가능
        validator.checkRolePassenger(UserRole.of(role));
        RefundRequest request = getRequest(requestId);
        PaymentIdentityView payment = paymentQueryPort.findIdentityViewById(request.getPaymentId()).orElseThrow(()-> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));
        //해당 결제의 승객만 취소 가능
        validator.checkPassengerPermission(userId, payment.getPassengerId());
        //환불 요청 상태가 REQUESTED 일때만 취소 가능
        validator.checkRefundRequestStatusRequested(request.getStatus());
        request.cancel();
        return new RefundRequestCancelResult(request.getId(), SuccessMessage.REQUEST_CANCEL_SUCCESS);
    }

    private RefundRequest getRequest(UUID requestId) {
        return requestQueryPort.findById(requestId).orElseThrow(() -> new PaymentException(PaymentErrorCode.REFUND_REQUEST_NOT_FOUND));
    }

    private PaymentAdminReadResult getPaymentAdminReadResult(Payment payment) {
        return new PaymentAdminReadResult(
                payment.getId(),
                payment.getAmount().value(),
                payment.getStatus().name(),
                payment.getMethod().name(),
                payment.getApprovedAt(),
                payment.getPassengerId(),
                payment.getDriverId(),
                payment.getTripId(),
                null,
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

    private RefundAdminReadResult getRefundReadResult(Refund refund) {
        return new RefundAdminReadResult(
                refund.getId(),
                refund.getStatus().name(),
                refund.getReason().getDescription(),
                refund.getDetailReason(),
                refund.getRequestId(),
                refund.getRefundedAt(),
                refund.getPayment().getId(),
                refund.getPayment().getAmount().value(),
                new PgRefundResult(refund.getTransactionKey(), refund.getPgFailReason(), refund.getCanceledAt()),
                refund.getCreatedAt(),
                refund.getUpdatedAt()
        );
    }
}
