package com.gooddaytaxi.payment.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.payment.application.command.refund.RefundCreateCommand;
import com.gooddaytaxi.payment.application.result.refund.RefundCreateResult;
import com.gooddaytaxi.payment.application.service.RefundService;
import com.gooddaytaxi.payment.presentation.external.dto.request.refund.RefundCreateRequestDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.refund.RefundCreateResponseDto;
import com.gooddaytaxi.payment.presentation.external.mapper.command.refund.RefundCreateMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.refund.RefundCreateResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @PostMapping("/tosspay/{paymentId}")
    public ResponseEntity<ApiResponse<RefundCreateResponseDto>> ConfirmTosspayRefund(@PathVariable UUID paymentId,
                                                                                     @RequestBody @Valid RefundCreateRequestDto requestDto,
                                                                                     @RequestParam String role) {
        RefundCreateCommand command = RefundCreateMapper.toCommand(requestDto);
        RefundCreateResult result = refundService.confirmTosspayRefund(paymentId, command, role);
        RefundCreateResponseDto responseDto = RefundCreateResponseMapper.toResponse(result);
        return ResponseEntity.status(201).body(ApiResponse.success(responseDto));
    }

    //디버그용 - 토스페이 외부결제 정보 조회
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<String>> getRefundInfo(@PathVariable UUID paymentId) {
        String dto = refundService.getExternalTossPay(paymentId);
        return ResponseEntity.status(201).body(ApiResponse.success(dto));
    }
}
