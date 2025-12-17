package com.gooddaytaxi.payment.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.payment.application.command.refund.RefundCreateCommand;
import com.gooddaytaxi.payment.application.command.refund.RefundSearchCommand;
import com.gooddaytaxi.payment.application.result.refund.RefundCreateResult;
import com.gooddaytaxi.payment.application.result.refund.RefundReadResult;
import com.gooddaytaxi.payment.application.service.RefundService;
import com.gooddaytaxi.payment.presentation.external.dto.request.refund.RefundCreateRequestDto;
import com.gooddaytaxi.payment.presentation.external.dto.request.refund.RefundSearchRequestDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.refund.RefundCreateResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.refund.RefundReadResponseDto;
import com.gooddaytaxi.payment.presentation.external.mapper.command.refund.RefundCreateMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.command.refund.RefundSearchMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.refund.RefundCreateResponseMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.refund.RefundReadResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    //결제수단이 토스페이인 경우 환불 요청
    @PostMapping("/tosspay/{paymentId}")
    public ResponseEntity<ApiResponse<RefundCreateResponseDto>> ConfirmTosspayRefund(@PathVariable UUID paymentId,
                                                                                     @RequestBody @Valid RefundCreateRequestDto requestDto,
                                                                                     @RequestHeader(value = "X-User-UUID") UUID userId,
                                                                                     @RequestHeader(value = "X-User-Role") String role) {
        RefundCreateCommand command = RefundCreateMapper.toTosspayCommand(requestDto);
        RefundCreateResult result = refundService.confirmTosspayRefund(paymentId, command, userId, role);
        RefundCreateResponseDto responseDto = RefundCreateResponseMapper.toResponse(result);
        return ResponseEntity.status(201).body(ApiResponse.success(responseDto));
    }

    //결제수단이 현금/카드인 경우 환불 내용 등록 - 이미 기사가 환불을 집행하고 관리자가 확인 후 등록
    @PostMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<RefundCreateResponseDto>> registerCashCardRefund(@PathVariable UUID paymentId,
                                                                                       @RequestBody @Valid RefundCreateRequestDto requestDto,
                                                                                       @RequestHeader(value = "X-User-UUID") UUID userId,
                                                                                       @RequestHeader(value = "X-User-Role") String role) {
        RefundCreateCommand command = RefundCreateMapper.toPhysicalCommand(requestDto);
        RefundCreateResult result = refundService.registerPhysicalRefund(paymentId, command, userId, role);
        RefundCreateResponseDto responseDto = RefundCreateResponseMapper.toResponse(result);
        return ResponseEntity.status(201).body(ApiResponse.success(responseDto));
    }

    //결제수단이 실물결제인경우 기사에게 환불 수행 필요 요청
    @PostMapping("/driver/support/{paymentId}/{reason}")
    public ResponseEntity<ApiResponse<RefundCreateResponseDto>> DriverSupportRefund(@PathVariable UUID paymentId,
                                                                                    @PathVariable String reason,
                                                                                    @RequestHeader(value = "X-User-UUID") UUID userId,
                                                                                    @RequestHeader(value = "X-User-Role") String role) {
        RefundCreateResult result = refundService.requestDriverSupportRefund(paymentId,reason, userId, role);
        RefundCreateResponseDto responseDto = RefundCreateResponseMapper.toResponse(result);
        return ResponseEntity.status(201).body(ApiResponse.success(responseDto));
    }


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

    //디버그용 - 토스페이 외부결제 정보 조회
    @GetMapping("/tosspay/{paymentId}")
    public ResponseEntity<ApiResponse<String>> getRefundInfo(@PathVariable UUID paymentId) {
        String dto = refundService.getExternalTossPay(paymentId);
        return ResponseEntity.status(201).body(ApiResponse.success(dto));
    }

}
