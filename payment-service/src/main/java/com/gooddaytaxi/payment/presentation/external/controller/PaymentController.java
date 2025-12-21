package com.gooddaytaxi.payment.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.payment.application.command.payment.*;
import com.gooddaytaxi.payment.application.result.payment.*;
import com.gooddaytaxi.payment.application.service.PaymentService;
import com.gooddaytaxi.payment.presentation.external.dto.request.payment.*;
import com.gooddaytaxi.payment.presentation.external.dto.response.payment.*;
import com.gooddaytaxi.payment.presentation.external.mapper.response.payment.PaymentUpdateResponseMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.command.payment.*;
import com.gooddaytaxi.payment.presentation.external.mapper.response.payment.PaymentCancelResponseMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.payment.PaymentReadResponseMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.payment.PaymentCreateResponseMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.payment.PaymentApproveResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    //결제 청구서 생성 - 실제로는 이벤트로만 생성, 이건 테스트용
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentCreateResponseDto>> createPayment(@RequestBody @Valid PaymentCreateRequestDto requestDto,
                                                                               @RequestHeader(value = "X-User-UUID", required = false) UUID userId) {
        PaymentCreateCommand command = PaymentCreateMapper.toCommand(requestDto);
        PaymentCreateResult result = paymentService.createPayment(command, userId);
        PaymentCreateResponseDto responseDto = PaymentCreateResponseMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(responseDto));
    }

    //tosspay 결제 준비 API
    @GetMapping("/tosspay/ready")
    public ResponseEntity<Void> readyPayment(@RequestParam UUID tripId) {
        UUID userId = UUID.fromString("d184e6d9-69ea-46cc-a6ae-3571e53c1d5f"); // 숭객
        String role = "PASSENGER"; // 승객
        log.info("toss.ready start: tripId: {}, userId: {}, role: {}", tripId, userId, role);

        // 1) 운행 요금 조회 + 결제 진행중으로 상태 변경
        Long fare = paymentService.tosspayReady(userId, role,tripId);

        // 1) 서버에서 주문/결제 정보 생성
        String orderId = "order-" + tripId;  // 실제로는 DB에 저장
        String customerKey = "customer-" + userId; // 예시
        long amount = fare; // 예: 운행 요금
        log.info("toss.ready ended: tripId: {}, userId: {}, role: {}, amount: {}", tripId, userId, role, amount);
        // 2) checkout.html 로 리다이렉트 (동적 값 전달)
        URI redirect = URI.create(
                "/passenger/payments/checkout.html" +
                        "?orderId=" + orderId +
                        "&customerKey=" + customerKey +
                        "&amount=" + amount
        );



        return ResponseEntity.status(HttpStatus.FOUND) // 302
                .location(redirect)
                .build();
    }

    //tosspay 결제 승인 API
    //외부에서 보기엔 toss결제 승인이므로 confirm으로 명명
    @PostMapping("/tosspay/confirm")
    public ResponseEntity<ApiResponse<PaymentApproveResponseDto>> confirmTossPayPayment(@RequestBody @Valid PaymentTossPayRequestDto requestDto,
                                                                                        @RequestHeader(value = "X-User-UUID", required = false) UUID userId,
                                                                                        @RequestHeader(value = "X-User-Role", required = false) String role) {
        PaymentTossPayCommand command = PaymentTossPayMapper.toCommand(requestDto);
        PaymentApproveResult result = paymentService.approveTossPayment(command,userId, role);
        PaymentApproveResponseDto responseDto = PaymentApproveResponseMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    //기사가 직접 결제후 완료 처리
    @PutMapping("/driver/{paymentId}/confirm")
    public ResponseEntity<ApiResponse<PaymentApproveResponseDto>> confirmDriverPayment(@PathVariable UUID paymentId,
                                                                                       @RequestHeader(value = "X-User-UUID") UUID userId,
                                                                                       @RequestHeader(value = "X-User-Role") String role) {

        PaymentApproveResult result = paymentService.approveDriverPayment(paymentId, userId, role);
        PaymentApproveResponseDto responseDto = PaymentApproveResponseMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    //결제 단건 조회 - 승객/기사용
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentReadResponseDto>> getPayment(@PathVariable UUID paymentId,
                                                                               @RequestHeader(value = "X-User-UUID") UUID userId,
                                                                               @RequestHeader(value = "X-User-Role") String role) {
        PaymentReadResult result = paymentService.getPayment(paymentId, userId, role);
        PaymentReadResponseDto responseDto = PaymentReadResponseMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    //결제 검색 기능
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PaymentReadResponseDto>>> searchPayment(@RequestBody @Valid PaymentSearchRequestDto requestDto,
                                                                                        @RequestHeader(value = "X-User-UUID") UUID userId,
                                                                                        @RequestHeader(value = "X-User-Role") String role) {
        PaymentSearchCommand command = PaymentSearchMapper.toCommand(requestDto);
        Page<PaymentReadResult> result = paymentService.searchPayment(command, userId, role);
        Page<PaymentReadResponseDto> responseDto = PaymentReadResponseMapper.toPageResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    //결제 전 금액 변경
    @PutMapping("/amount")
    public ResponseEntity<ApiResponse<PaymentUpdateResponseDto>> changePaymentAmount(@RequestBody @Valid PaymentAmountChangeRequestDto requestDto,
                                                                                     @RequestHeader(value = "X-User-UUID") UUID userId,
                                                                                     @RequestHeader(value = "X-User-Role") String role) {
        PaymentAmountChangeCommand command = PaymentUpdateMapper.toAmountCommand(requestDto);
        PaymentUpdateResult result = paymentService.changePaymentAmount(command, userId, role);
        PaymentUpdateResponseDto responseDto = PaymentUpdateResponseMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    //결제 전 결제 수단 변경
    @PutMapping("/method")
    public ResponseEntity<ApiResponse<PaymentUpdateResponseDto>> changePaymentMethod(@RequestBody @Valid PaymentMethodChangeRequestDto requestDto,
                                                                                     @RequestHeader(value = "X-User-UUID") UUID userId,
                                                                                     @RequestHeader(value = "X-User-Role") String role) {
        PaymentMethodChangeCommand command = PaymentUpdateMapper.toMethodCommand(requestDto);
        PaymentUpdateResult result = paymentService.changePaymentMethod(command, userId, role);
        PaymentUpdateResponseDto responseDto = PaymentUpdateResponseMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }
    //결제 취소
    @DeleteMapping
    public ResponseEntity<ApiResponse<PaymentCancelResponseDto>> cancelPayment(@RequestBody @Valid PaymentCancelRequestDto requestDto,
                                                                               @RequestHeader(value = "X-User-UUID") UUID userId,
                                                                               @RequestHeader(value = "X-User-Role") String role) {
        PaymentCancelCommand command = PaymentCancelMapper.toCommand(requestDto);
        PaymentCancelResult result = paymentService.cancelPayment(command, userId, role);
        PaymentCancelResponseDto responseDto = PaymentCancelResponseMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

}
