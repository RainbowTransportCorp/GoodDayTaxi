package com.gooddaytaxi.payment.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.payment.application.command.payment.PaymentSearchCommand;
import com.gooddaytaxi.payment.application.command.refund.RefundCreateCommand;
import com.gooddaytaxi.payment.application.command.refund.RefundSearchCommand;
import com.gooddaytaxi.payment.application.command.refundRequest.RefundReqeustSearchCommand;
import com.gooddaytaxi.payment.application.command.refundRequest.RefundRequestResponseCreateCommand;
import com.gooddaytaxi.payment.application.result.payment.PaymentAdminReadResult;
import com.gooddaytaxi.payment.application.result.refund.RefundAdminReadResult;
import com.gooddaytaxi.payment.application.result.refund.RefundCreateResult;
import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestAdminReadResult;
import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestCreateResult;
import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestReadResult;
import com.gooddaytaxi.payment.application.service.PaymentService;
import com.gooddaytaxi.payment.application.service.RefundRequestService;
import com.gooddaytaxi.payment.application.service.RefundService;
import com.gooddaytaxi.payment.infrastructure.security.UserPrincipal;
import com.gooddaytaxi.payment.presentation.external.dto.request.refund.RefundCreateRequestDto;
import com.gooddaytaxi.payment.presentation.external.dto.request.requestRefund.RefundRequestResponseRequestDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.payment.PaymentAdminReadResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.refund.RefundAdminReadResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.refund.RefundCreateResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.reqeustRufund.RefundReqeustCreateResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.reqeustRufund.RefundRequestAdminReadResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.reqeustRufund.RefundRequestReadResponseDto;
import com.gooddaytaxi.payment.presentation.external.mapper.command.payment.PaymentSearchMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.command.refund.RefundCreateMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.command.refund.RefundSearchMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.command.requestFefund.RefundRequestCreateMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.command.requestFefund.RefundRequestSearchMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.payment.PaymentReadResponseMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.refund.RefundCreateResponseMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.refund.RefundReadResponseMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.requestFefund.RefundRequestCreateResponseMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.requestFefund.RefundRequestReadResponseMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 결제 / 환불 / 환불요청 관리자용 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class PaymentAdminController {

    private final PaymentService paymentService;
    private final RefundRequestService requestService;
    private final RefundService refundService;

    // =========================
    // 결제 단건 조회
    // =========================
    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentAdminReadResponseDto>> getPayment(
        @PathVariable UUID paymentId,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        PaymentAdminReadResult result =
            paymentService.getAdminPayment(
                paymentId,
                user.role().name()
            );

        return ResponseEntity.ok(
            ApiResponse.success(
                PaymentReadResponseMapper.toAdminResponse(result)
            )
        );
    }

    // =========================
    // 결제 검색
    // =========================
    @GetMapping("/payments/search")
    public ResponseEntity<ApiResponse<Page<PaymentAdminReadResponseDto>>> searchPayment(
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size,
        @RequestParam(required = false) String method,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) UUID passengerId,
        @RequestParam(required = false) UUID driverId,
        @RequestParam(required = false) UUID tripId,
        @RequestParam @NotBlank String searchPeriod,
        @RequestParam(required = false) String startDay,
        @RequestParam(required = false) String endDay,
        @RequestParam(required = false) String sortBy,
        @RequestParam(name = "sortAscending", required = false) Boolean sortAscending,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        PaymentSearchCommand command =
            PaymentSearchMapper.toAdminCommand(
                page, size, method, status,
                passengerId, driverId, tripId,
                searchPeriod, startDay, endDay,
                sortBy, sortAscending
            );

        Page<PaymentAdminReadResult> result =
            paymentService.searchAdminPayment(
                command,
                user.role().name()
            );

        return ResponseEntity.ok(
            ApiResponse.success(
                PaymentReadResponseMapper.toPageAdminResponse(result)
            )
        );
    }

    // =========================
    // 환불 요청 단건 조회
    // =========================
    @GetMapping("/refund/requests/{requestId}")
    public ResponseEntity<ApiResponse<RefundRequestAdminReadResponseDto>> getRefundRequest(
        @PathVariable UUID requestId,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        RefundRequestAdminReadResult result =
            requestService.getAdminRefundRequest(
                requestId,
                user.role().name()
            );

        return ResponseEntity.ok(
            ApiResponse.success(
                RefundRequestReadResponseMapper.toAdminResponse(result)
            )
        );
    }

    // =========================
    // 환불 요청 검색
    // =========================
    @GetMapping("/refund/requests/search")
    public ResponseEntity<ApiResponse<Page<RefundRequestReadResponseDto>>> searchRefundRequests(
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size,
        @RequestParam(required = false) UUID paymentId,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String reasonKeyword,
        @RequestParam(required = false) String method,
        @RequestParam(required = false) UUID passengerId,
        @RequestParam(required = false) UUID driverId,
        @RequestParam @NotBlank String searchPeriod,
        @RequestParam(required = false) String startDay,
        @RequestParam(required = false) String endDay,
        @RequestParam(required = false) String sortBy,
        @RequestParam(name = "sortAscending", required = false) Boolean sortAscending,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        RefundReqeustSearchCommand command =
            RefundRequestSearchMapper.toAdminCommand(
                page, size, paymentId, status, reasonKeyword,
                method, passengerId, driverId,
                searchPeriod, startDay, endDay,
                sortBy, sortAscending
            );

        Page<RefundRequestReadResult> results =
            requestService.searchAdminRefundRequests(
                command,
                user.role().name()
            );

        return ResponseEntity.ok(
            ApiResponse.success(
                RefundRequestReadResponseMapper.toPageResponse(results)
            )
        );
    }

    // =========================
    // 환불 요청 응답
    // =========================
    @PostMapping("/refund/requests/response")
    public ResponseEntity<ApiResponse<RefundReqeustCreateResponseDto>> respondToRefundRequest(
        @RequestBody @Valid RefundRequestResponseRequestDto requestDto,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        RefundRequestResponseCreateCommand command =
            RefundRequestCreateMapper.toResponseCommand(requestDto);

        RefundRequestCreateResult result =
            requestService.respondToRefundRequest(
                command,
                user.userId(),
                user.role().name()
            );

        return ResponseEntity.ok(
            ApiResponse.success(
                RefundRequestCreateResponseMapper.toResponse(result)
            )
        );
    }

    // =========================
    // 토스페이 환불 승인
    // =========================
    @PostMapping("/refunds/tosspay/{paymentId}")
    public ResponseEntity<ApiResponse<RefundCreateResponseDto>> confirmTosspayRefund(
        @PathVariable UUID paymentId,
        @RequestBody @Valid RefundCreateRequestDto requestDto,
        @RequestHeader("Idempotency-Key") String idempotencyKey,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        RefundCreateCommand command =
            RefundCreateMapper.toTosspayCommand(requestDto);

        RefundCreateResult result =
            refundService.confirmTosspayRefund(
                paymentId,
                command,
                user.userId(),
                user.role().name(),
                idempotencyKey
            );

        return ResponseEntity.ok(
            ApiResponse.success(
                RefundCreateResponseMapper.toResponse(result)
            )
        );
    }

    // =========================
    // 기사 환불 지원 요청
    // =========================
    @PostMapping("/refunds/driver/support/{paymentId}/{reason}")
    public ResponseEntity<ApiResponse<RefundCreateResponseDto>> requestDriverSupportRefund(
        @PathVariable UUID paymentId,
        @PathVariable String reason,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        RefundCreateResult result =
            refundService.requestDriverSupportRefund(
                paymentId,
                reason,
                user.userId(),
                user.role().name()
            );

        return ResponseEntity.ok(
            ApiResponse.success(
                RefundCreateResponseMapper.toResponse(result)
            )
        );
    }

    // =========================
    // 현금/카드 환불 등록
    // =========================
    @PostMapping("/refunds/{paymentId}")
    public ResponseEntity<ApiResponse<RefundCreateResponseDto>> registerCashCardRefund(
        @PathVariable UUID paymentId,
        @RequestBody @Valid RefundCreateRequestDto requestDto,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        RefundCreateCommand command =
            RefundCreateMapper.toPhysicalCommand(requestDto);

        RefundCreateResult result =
            refundService.registerPhysicalRefund(
                paymentId,
                command,
                user.userId(),
                user.role().name()
            );

        return ResponseEntity.ok(
            ApiResponse.success(
                RefundCreateResponseMapper.toResponse(result)
            )
        );
    }

    // =========================
    // 환불 단건 조회
    // =========================
    @GetMapping("/refunds/{paymentId}")
    public ResponseEntity<ApiResponse<RefundAdminReadResponseDto>> getRefundStatus(
        @PathVariable UUID paymentId,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        RefundAdminReadResult result =
            refundService.getAdminRefund(
                paymentId,
                user.role().name()
            );

        return ResponseEntity.ok(
            ApiResponse.success(
                RefundReadResponseMapper.toAdminResponse(result)
            )
        );
    }

    // =========================
    // 환불 검색
    // =========================
    @GetMapping("/refunds/search")
    public ResponseEntity<ApiResponse<Page<RefundAdminReadResponseDto>>> searchRefund(
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String reason,
        @RequestParam(name = "existRequest", required = false) Boolean existRequest,
        @RequestParam(required = false) UUID passengerId,
        @RequestParam(required = false) UUID driverId,
        @RequestParam(required = false) UUID tripId,
        @RequestParam(required = false) String method,
        @RequestParam(required = false) Long minAmount,
        @RequestParam(required = false) Long maxAmount,
        @RequestParam @NotBlank String searchPeriod,
        @RequestParam(required = false) String startDay,
        @RequestParam(required = false) String endDay,
        @RequestParam(required = false) String sortBy,
        @RequestParam(name = "sortAscending", required = false) Boolean sortAscending,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        RefundSearchCommand command =
            RefundSearchMapper.toAdminCommand(
                page, size, status, reason, existRequest,
                passengerId, driverId, tripId,
                method, minAmount, maxAmount,
                searchPeriod, startDay, endDay,
                sortBy, sortAscending
            );

        Page<RefundAdminReadResult> result =
            refundService.searchAdminRefund(
                command,
                user.role().name()
            );

        return ResponseEntity.ok(
            ApiResponse.success(
                RefundReadResponseMapper.toPageAdminResponse(result)
            )
        );
    }

    // =========================
    // 디버그용 토스페이 외부 조회
    // =========================
    @GetMapping("/tosspay/{paymentId}")
    public ResponseEntity<ApiResponse<String>> getRefundInfo(
        @PathVariable UUID paymentId
    ) {
        return ResponseEntity.ok(
            ApiResponse.success(
                refundService.getExternalTossPay(paymentId)
            )
        );
    }
}
