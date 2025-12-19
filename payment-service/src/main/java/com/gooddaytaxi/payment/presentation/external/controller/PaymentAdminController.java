package com.gooddaytaxi.payment.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.payment.application.command.payment.PaymentSearchCommand;
import com.gooddaytaxi.payment.application.result.payment.PaymentAdminReadResult;
import com.gooddaytaxi.payment.application.service.PaymentService;
import com.gooddaytaxi.payment.presentation.external.dto.request.payment.PaymentSearchRequestDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.payment.PaymentAdminReadResponseDto;
import com.gooddaytaxi.payment.presentation.external.mapper.command.payment.PaymentSearchMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.payment.PaymentReadResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/*
* 결제, 환불 요청, 환불 관리자용 controller
* */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/payments")
public class PaymentAdminController {
    private final PaymentService paymentService;

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentAdminReadResponseDto>> getPayment(@PathVariable UUID paymentId,
                                                                               @RequestHeader(value = "X-User-UUID") UUID userId,
                                                                               @RequestHeader(value = "X-User-Role") String role) {
        PaymentAdminReadResult result = paymentService.getAdminPayment(paymentId, userId, role);
        PaymentAdminReadResponseDto responseDto = PaymentReadResponseMapper.toAdminResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    //결제 검색 기능
//    @GetMapping("/search")
//    public ResponseEntity<ApiResponse<Page<PaymentAdminReadResponseDto>>> searchPayment(@RequestBody @Valid PaymentSearchRequestDto requestDto,
//                                                                                        @RequestHeader(value = "X-User-UUID") UUID userId,
//                                                                                        @RequestHeader(value = "X-User-Role") String role) {
//        PaymentSearchCommand command = PaymentSearchMapper.toCommand(requestDto);
//        Page<PaymentAdminReadResult> result = paymentService.searchAdminPayment(command, userId, role);
//        Page<PaymentAdminReadResponseDto> responseDto = PaymentReadResponseMapper.toPageResponse(result);
//        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
//    }



}
