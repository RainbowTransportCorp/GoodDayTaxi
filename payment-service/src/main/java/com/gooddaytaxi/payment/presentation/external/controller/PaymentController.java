package com.gooddaytaxi.payment.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.payment.application.command.PaymentCreateCommand;
import com.gooddaytaxi.payment.application.command.PaymentTossPayCommand;
import com.gooddaytaxi.payment.application.result.PaymentCreateResult;
import com.gooddaytaxi.payment.application.result.PaymentTossPayResult;
import com.gooddaytaxi.payment.application.service.PaymentService;
import com.gooddaytaxi.payment.presentation.external.dto.request.PaymentCreateRequestDto;
import com.gooddaytaxi.payment.presentation.external.dto.request.PaymentTossPayRequestDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.PaymentCreateResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.PaymentTossPayResponseDto;
import com.gooddaytaxi.payment.presentation.external.mapper.command.PaymentCreateMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.command.PaymentTossPayMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.PaymentCreateResponseMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.PaymentTossPayResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    //결제 청구서 생성
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentCreateResponseDto>> createPayment(@RequestBody @Valid PaymentCreateRequestDto requestDto) {
        PaymentCreateCommand command = PaymentCreateMapper.toCommand(requestDto);
        PaymentCreateResult result = paymentService.createPayment(command);
        PaymentCreateResponseDto responseDto = PaymentCreateResponseMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(responseDto));
    }

    //tosspay 결제 준비 API
    @GetMapping("/tosspay/ready")
    public ResponseEntity<Void> readyPayment(@RequestParam UUID userId,
                                             @RequestParam String role,
                                             @RequestParam UUID tripId) {

        // 1) 운행 요금 조회 + 결제 진행중으로 상태 변경
        Long fare = paymentService.tosspayReady(userId, role,tripId);

        // 1) 서버에서 주문/결제 정보 생성
        String orderId = "order-" + tripId;  // 실제로는 DB에 저장
        String customerKey = "customer-" + userId; // 예시
        long amount = fare; // 예: 운행 요금

        // 2) checkout.html 로 리다이렉트 (동적 값 전달)
        URI redirect = URI.create(
                "/checkout.html" +
                        "?orderId=" + orderId +
                        "&customerKey=" + customerKey +
                        "&amount=" + amount
        );


        return ResponseEntity.status(HttpStatus.FOUND) // 302
                .location(redirect)
                .build();
    }

    //tosspay 결제 승인 API
    @PostMapping("/tosspay/confirm")
    public ResponseEntity<ApiResponse<PaymentTossPayResponseDto>> requestTossPayPayment(@RequestBody @Valid PaymentTossPayRequestDto requestDto) {
        PaymentTossPayCommand command = PaymentTossPayMapper.toCommand(requestDto);
        PaymentTossPayResult result = paymentService.confirmTossPayment(command);
        PaymentTossPayResponseDto responseDto = PaymentTossPayResponseMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    //기사가 직접 결제후 완료 처리
    @PutMapping("/driverpay/{tripId}/confirm")
    public ResponseEntity<ApiResponse<PaymentTossPayResponseDto>> requestDriverPayPayment(@PathVariable UUID tripId,
                                                                                          @RequestParam UUID userId,
                                                                                          @RequestParam String role) {

        PaymentTossPayResult result = paymentService.requestDriverPayPayment(tripId, userId, role);
        PaymentTossPayResponseDto responseDto = PaymentTossPayResponseMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

}
