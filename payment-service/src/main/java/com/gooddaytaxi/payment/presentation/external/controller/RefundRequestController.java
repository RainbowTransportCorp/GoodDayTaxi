package com.gooddaytaxi.payment.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.payment.application.command.refundRequest.RefundReqeustSearchCommand;
import com.gooddaytaxi.payment.application.command.refundRequest.RefundRequestCreateCommand;
import com.gooddaytaxi.payment.application.command.refundRequest.RefundRequestResponseCreateCommand;
import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestCancelResult;
import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestCreateResult;
import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestReadResult;
import com.gooddaytaxi.payment.application.service.RefundRequestService;
import com.gooddaytaxi.payment.presentation.external.dto.request.requestRefund.RefundRequestSearchRequestDto;
import com.gooddaytaxi.payment.presentation.external.dto.request.requestRefund.RefundRequestResponseRequestDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.reqeustRufund.RefundReqeustCancelResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.reqeustRufund.RefundReqeustCreateResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.request.requestRefund.RefundRequestCreateRequestDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.reqeustRufund.RefundRequestReadResponseDto;
import com.gooddaytaxi.payment.presentation.external.mapper.command.requestFefund.RefundRequestCreateMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.command.requestFefund.RefundRequestSearchMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.requestFefund.RefundRequestCancelResponseMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.requestFefund.RefundRequestCreateResponseMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.requestFefund.RefundRequestReadResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/refund/requests")
@RequiredArgsConstructor
public class RefundRequestController {
    private final RefundRequestService requestService;

    //환불 요청 생성
    @PostMapping
    public ResponseEntity<ApiResponse<RefundReqeustCreateResponseDto>> createRefundRequest(@RequestBody @Valid RefundRequestCreateRequestDto requestDto,
                                                                                           @RequestParam UUID userId,
                                                                                            @RequestParam String role) {
        RefundRequestCreateCommand command = RefundRequestCreateMapper.toRequestCommand(requestDto);
        RefundRequestCreateResult result = requestService.createRefundRequest(command, userId, role);
        RefundReqeustCreateResponseDto responseDto = RefundRequestCreateResponseMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    //환불 요청 단건 조회
    @GetMapping("/{requestId}")
    public ResponseEntity<ApiResponse<RefundRequestReadResponseDto>> getRefundRequest(@PathVariable UUID requestId,
                                                                                      @RequestParam UUID userId,
                                                                                      @RequestParam String role) {
        RefundRequestReadResult result = requestService.getRefundRequest(requestId, userId, role);
        RefundRequestReadResponseDto responseDto = RefundRequestReadResponseMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<RefundRequestReadResponseDto>>> searchRefundRequests(@RequestBody @Valid RefundRequestSearchRequestDto requestDto,
                                                                         @RequestParam UUID userId,
                                                                         @RequestParam String role) {
        RefundReqeustSearchCommand command = RefundRequestSearchMapper.toCommand(requestDto);
        Page<RefundRequestReadResult> results = requestService.searchRefundRequests(command, userId, role);
        Page<RefundRequestReadResponseDto> responseDtos = RefundRequestReadResponseMapper.toPageResponse(results);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDtos));


    }

    //환불 요청 응답
    @PostMapping("/response")
    public ResponseEntity<ApiResponse<RefundReqeustCreateResponseDto>> respondToRefundRequest(@RequestBody @Valid RefundRequestResponseRequestDto requestDto,
                                                                                              @RequestParam UUID userId,
                                                                                              @RequestParam String role) {
        RefundRequestResponseCreateCommand command = RefundRequestCreateMapper.toResponseCommand(requestDto);
        RefundRequestCreateResult result = requestService.respondToRefundRequest(command, userId, role);
        RefundReqeustCreateResponseDto responseDto = RefundRequestCreateResponseMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    //환불 요청 취소
    @DeleteMapping("/{requestId}")
    public ResponseEntity<ApiResponse<RefundReqeustCancelResponseDto>> cancelRefundRequest(@PathVariable UUID requestId,
                                                                    @RequestParam UUID userId) {
        RefundRequestCancelResult result = requestService.cancelRefundRequest(requestId, userId);
        RefundReqeustCancelResponseDto responseDto = RefundRequestCancelResponseMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }
}
