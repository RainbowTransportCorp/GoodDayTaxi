package com.gooddaytaxi.payment.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.payment.application.command.refund.RefundSearchCommand;
import com.gooddaytaxi.payment.application.result.refund.RefundReadResult;
import com.gooddaytaxi.payment.application.service.RefundService;
import com.gooddaytaxi.payment.presentation.external.dto.response.refund.RefundReadResponseDto;
import com.gooddaytaxi.payment.presentation.external.mapper.command.refund.RefundSearchMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.refund.RefundReadResponseMapper;
import jakarta.validation.constraints.NotBlank;
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
    public ResponseEntity<ApiResponse<Page<RefundReadResponseDto>>> searchRefund(@RequestParam(required = false) Integer page,
                                                                                 @RequestParam(required = false) Integer size,
                                                                                 @RequestParam(required = false) String status,
                                                                                 @RequestParam(required = false) String reason,
                                                                                 @RequestParam(name = "existRequest", required = false) Boolean existRequest,
                                                                                 @RequestParam(required = false) UUID tripId,
                                                                                 @RequestParam(required = false) String method,
                                                                                 @RequestParam(required = false) Long minAmount,
                                                                                 @RequestParam(required = false) Long maxAmount,
                                                                                 @RequestParam @NotBlank String searchPeriod,
                                                                                 @RequestParam(required = false) String startDay,
                                                                                 @RequestParam(required = false) String endDay,
                                                                                 @RequestParam(required = false) String sortBy,
                                                                                 @RequestParam(name = "sortAscending", required = false) Boolean sortAscending,
                                                                                      @RequestHeader(value = "X-User-UUID") UUID userId,
                                                                                      @RequestHeader(value = "X-User-Role") String role) {
        RefundSearchCommand command = RefundSearchMapper.toCommand(page, size, status, reason, existRequest, tripId, method, minAmount, maxAmount, searchPeriod, startDay, endDay, sortBy, sortAscending);
        Page<RefundReadResult> result = refundService.searchRefund(command, userId, role);
        Page<RefundReadResponseDto> responseDto = RefundReadResponseMapper.toPageResponse(result);
        return ResponseEntity.status(200).body(ApiResponse.success(responseDto));
    }



}
