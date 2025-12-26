package com.gooddaytaxi.payment.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.common.core.dto.PageResponse;
import com.gooddaytaxi.payment.application.command.payment.*;
import com.gooddaytaxi.payment.application.result.payment.*;
import com.gooddaytaxi.payment.application.service.PaymentService;
import com.gooddaytaxi.payment.infrastructure.security.UserPrincipal;
import com.gooddaytaxi.payment.presentation.external.dto.request.payment.*;
import com.gooddaytaxi.payment.presentation.external.dto.response.payment.*;
import com.gooddaytaxi.payment.presentation.external.mapper.command.payment.*;
import com.gooddaytaxi.payment.presentation.external.mapper.response.payment.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    // ===============================
    // 결제 청구서 생성 (테스트용)
    // ===============================
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentCreateResponseDto>> createPayment(
        @RequestBody @Valid PaymentCreateRequestDto requestDto,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        PaymentCreateCommand command = PaymentCreateMapper.toCommand(requestDto);
        PaymentCreateResult result =
            paymentService.createPayment(command, user.userId());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                PaymentCreateResponseMapper.toResponse(result)
            ));
    }

    // ===============================
    // 토스페이 결제 준비 (redirect)
    // ===============================
    @GetMapping("/tosspay/ready")
    public ResponseEntity<Void> readyPayment(
        @RequestParam UUID tripId,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        log.info(
            "toss.ready start: tripId={}, userId={}, role={}",
            tripId, user.userId(), user.role()
        );

        Long fare =
            paymentService.tosspayReady(
                user.userId(),
                user.role().name(),
                tripId
            );

        String orderId = "order-" + tripId;
        String customerKey = "customer-" + user.userId();

        URI redirect = URI.create(
            "/passenger/payments/checkout.html" +
                "?orderId=" + orderId +
                "&customerKey=" + customerKey +
                "&amount=" + fare
        );

        return ResponseEntity.status(HttpStatus.FOUND)
            .location(redirect)
            .build();
    }

    // ===============================
    // 토스페이 결제 승인
    // ===============================
    @PostMapping("/tosspay/confirm")
    public ResponseEntity<ApiResponse<PaymentApproveResponseDto>> confirmTossPayPayment(
        @RequestBody @Valid PaymentTossPayRequestDto requestDto,
        @RequestHeader("Idempotency-Key") String idempotencyKey,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        PaymentTossPayCommand command =
            PaymentTossPayMapper.toCommand(requestDto);

        PaymentApproveResult result =
            paymentService.approveTossPayment(
                command,
                user.userId(),
                user.role().name(),
                idempotencyKey
            );

        return ResponseEntity.ok(
            ApiResponse.success(
                PaymentApproveResponseMapper.toResponse(result)
            )
        );
    }

    // ===============================
    // 기사 직접 결제 완료 처리
    // ===============================
    @PutMapping("/driver/{paymentId}/confirm")
    public ResponseEntity<ApiResponse<PaymentApproveResponseDto>> confirmDriverPayment(
        @PathVariable UUID paymentId,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        PaymentApproveResult result =
            paymentService.approveDriverPayment(
                paymentId,
                user.userId(),
                user.role().name()
            );

        return ResponseEntity.ok(
            ApiResponse.success(
                PaymentApproveResponseMapper.toResponse(result)
            )
        );
    }

    // ===============================
    // 결제 단건 조회 (tripId 기준)
    // ===============================
    @GetMapping("/trip/{tripId}/payment")
    public ResponseEntity<ApiResponse<PaymentReadResponseDto>> getPaymentByTripId(
        @PathVariable UUID tripId,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        PaymentReadResult result =
            paymentService.getPaymentByTripId(
                tripId,
                user.userId(),
                user.role().name()
            );

        return ResponseEntity.ok(
            ApiResponse.success(
                PaymentReadResponseMapper.toResponse(result)
            )
        );
    }

    // ===============================
    // 결제 단건 조회 (paymentId)
    // ===============================
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentReadResponseDto>> getPayment(
        @PathVariable UUID paymentId,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        PaymentReadResult result =
            paymentService.getPayment(
                paymentId,
                user.userId(),
                user.role().name()
            );

        return ResponseEntity.ok(
            ApiResponse.success(
                PaymentReadResponseMapper.toResponse(result)
            )
        );
    }

    // ===============================
    // 결제 검색
    // ===============================
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<PaymentReadResponseDto>>> searchPayment(
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size,
        @RequestParam(required = false) String method,
        @RequestParam(required = false) String status,
        @RequestParam @NotBlank String searchPeriod,
        @RequestParam(required = false) String startDay,
        @RequestParam(required = false) String endDay,
        @RequestParam(required = false) String sortBy,
        @RequestParam(name = "sortAscending", required = false) Boolean sortAscending,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        PaymentSearchCommand command =
            PaymentSearchMapper.toCommand(
                page, size, method, status,
                searchPeriod, startDay, endDay,
                sortBy, sortAscending
            );

        Page<PaymentReadResult> pageResult =
            paymentService.searchPayment(
                command,
                user.userId(),
                user.role().name()
            );

        PageResponse<PaymentReadResponseDto> response =
            new PageResponse<>(
                pageResult.getContent().stream()
                    .map(PaymentReadResponseMapper::toResponse)
                    .toList(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isLast()
            );

        return ResponseEntity.ok(ApiResponse.success(response));
    }


    // ===============================
    // 결제 전 금액 변경
    // ===============================
    @PutMapping("/amount")
    public ResponseEntity<ApiResponse<PaymentUpdateResponseDto>> changePaymentAmount(
        @RequestBody @Valid PaymentAmountChangeRequestDto requestDto,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        PaymentAmountChangeCommand command =
            PaymentUpdateMapper.toAmountCommand(requestDto);

        PaymentUpdateResult result =
            paymentService.changePaymentAmount(
                command,
                user.userId(),
                user.role().name()
            );

        return ResponseEntity.ok(
            ApiResponse.success(
                PaymentUpdateResponseMapper.toResponse(result)
            )
        );
    }

    // ===============================
    // 결제 전 결제 수단 변경
    // ===============================
    @PutMapping("/method")
    public ResponseEntity<ApiResponse<PaymentUpdateResponseDto>> changePaymentMethod(
        @RequestBody @Valid PaymentMethodChangeRequestDto requestDto,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        PaymentMethodChangeCommand command =
            PaymentUpdateMapper.toMethodCommand(requestDto);

        PaymentUpdateResult result =
            paymentService.changePaymentMethod(
                command,
                user.userId(),
                user.role().name()
            );

        return ResponseEntity.ok(
            ApiResponse.success(
                PaymentUpdateResponseMapper.toResponse(result)
            )
        );
    }

    // ===============================
    // 결제 취소
    // ===============================
    @DeleteMapping
    public ResponseEntity<ApiResponse<PaymentCancelResponseDto>> cancelPayment(
        @RequestBody @Valid PaymentCancelRequestDto requestDto,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        PaymentCancelCommand command =
            PaymentCancelMapper.toCommand(requestDto);

        PaymentCancelResult result =
            paymentService.cancelPayment(
                command,
                user.userId(),
                user.role().name()
            );

        return ResponseEntity.ok(
            ApiResponse.success(
                PaymentCancelResponseMapper.toResponse(result)
            )
        );
    }
}
