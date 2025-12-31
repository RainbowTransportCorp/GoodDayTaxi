package com.gooddaytaxi.payment.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.payment.application.command.refundRequest.RefundReqeustSearchCommand;
import com.gooddaytaxi.payment.application.command.refundRequest.RefundRequestCreateCommand;
import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestCancelResult;
import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestCreateResult;
import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestReadResult;
import com.gooddaytaxi.payment.application.service.RefundRequestService;
import com.gooddaytaxi.payment.infrastructure.security.UserPrincipal;
import com.gooddaytaxi.payment.presentation.external.dto.request.requestRefund.RefundRequestCreateRequestDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.reqeustRufund.RefundReqeustCancelResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.reqeustRufund.RefundReqeustCreateResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.reqeustRufund.RefundRequestReadResponseDto;
import com.gooddaytaxi.payment.presentation.external.mapper.command.requestFefund.RefundRequestCreateMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.command.requestFefund.RefundRequestSearchMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.requestFefund.RefundRequestCancelResponseMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.requestFefund.RefundRequestCreateResponseMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.requestFefund.RefundRequestReadResponseMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/refund/requests")
@RequiredArgsConstructor
public class RefundRequestController {

    private final RefundRequestService requestService;

    // =========================
    // 환불 요청 생성
    // =========================
    @PostMapping
    public ResponseEntity<ApiResponse<RefundReqeustCreateResponseDto>> createRefundRequest(
        @RequestBody @Valid RefundRequestCreateRequestDto requestDto,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        RefundRequestCreateCommand command =
            RefundRequestCreateMapper.toRequestCommand(requestDto);

        RefundRequestCreateResult result =
            requestService.createRefundRequest(
                command,
                user.userId(),
                user.role().name() // 🔥 String role 유지
            );

        RefundReqeustCreateResponseDto responseDto =
            RefundRequestCreateResponseMapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    // =========================
    // 환불 요청 단건 조회
    // =========================
    @GetMapping("/{requestId}")
    public ResponseEntity<ApiResponse<RefundRequestReadResponseDto>> getRefundRequest(
        @PathVariable UUID requestId,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        RefundRequestReadResult result =
            requestService.getRefundRequest(
                requestId,
                user.userId(),
                user.role().name()
            );

        RefundRequestReadResponseDto responseDto =
            RefundRequestReadResponseMapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    // =========================
    // 환불 요청 검색
    // =========================
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<RefundRequestReadResponseDto>>> searchRefundRequests(
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String reasonKeyword,
        @RequestParam @NotBlank String searchPeriod,
        @RequestParam(required = false) String startDay,
        @RequestParam(required = false) String endDay,
        @RequestParam(required = false) String sortBy,
        @RequestParam(name = "sortAscending", required = false) Boolean sortAscending,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        RefundReqeustSearchCommand command =
            RefundRequestSearchMapper.toCommand(
                page, size, status, reasonKeyword,
                searchPeriod, startDay, endDay,
                sortBy, sortAscending
            );

        Page<RefundRequestReadResult> results =
            requestService.searchRefundRequests(
                command,
                user.userId(),
                user.role().name()
            );

        Page<RefundRequestReadResponseDto> responseDtos =
            RefundRequestReadResponseMapper.toPageResponse(results);

        return ResponseEntity.ok(ApiResponse.success(responseDtos));
    }

    // =========================
    // 환불 요청 취소
    // =========================
    @DeleteMapping("/{requestId}")
    public ResponseEntity<ApiResponse<RefundReqeustCancelResponseDto>> cancelRefundRequest(
        @PathVariable UUID requestId,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        RefundRequestCancelResult result =
            requestService.cancelRefundRequest(
                requestId,
                user.userId(),
                user.role().name()
            );

        RefundReqeustCancelResponseDto responseDto =
            RefundRequestCancelResponseMapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }
}
