package com.gooddaytaxi.payment.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.payment.application.command.refund.RefundSearchCommand;
import com.gooddaytaxi.payment.application.result.refund.RefundReadResult;
import com.gooddaytaxi.payment.application.service.RefundService;
import com.gooddaytaxi.payment.presentation.external.dto.request.refund.RefundSearchRequestDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.refund.RefundReadResponseDto;
import com.gooddaytaxi.payment.presentation.external.mapper.command.refund.RefundSearchMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.refund.RefundReadResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
/*
* 승객/기사용 환불 controller
* */
@RestController
@RequestMapping("/api/v1/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;


    //환불 상태 조회
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<RefundReadResponseDto>> getRefundStatus(@PathVariable UUID paymentId,
                                                                                   @RequestHeader(value = "X-User-UUID") UUID userId,
                                                                                   @RequestHeader(value = "X-User-Role") String role) {
        RefundReadResult result = refundService.getRefund(paymentId, userId, role);
        RefundReadResponseDto responseDto = RefundReadResponseMapper.toResponse(result);
        return ResponseEntity.status(200).body(ApiResponse.success(responseDto));

    }

    //환불 검색
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<RefundReadResponseDto>>> searchRefund(@RequestBody @Valid RefundSearchRequestDto requestDto,
                                                                                      @RequestHeader(value = "X-User-UUID") UUID userId,
                                                                                      @RequestHeader(value = "X-User-Role") String role) {
        RefundSearchCommand command = RefundSearchMapper.toCommand(requestDto);
        Page<RefundReadResult> result = refundService.searchRefund(command, userId, role);
        Page<RefundReadResponseDto> responseDto = RefundReadResponseMapper.toPageResponse(result);
        return ResponseEntity.status(200).body(ApiResponse.success(responseDto));
    }



}
