package com.gooddaytaxi.payment.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.payment.application.command.refundRequest.RefundRequestCreateCommand;
import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestCreateResult;
import com.gooddaytaxi.payment.application.service.RefundRequestService;
import com.gooddaytaxi.payment.presentation.external.dto.response.reqeustRufund.RefundReqeustCreateResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.request.requestRefund.RefundRequestCreateRequestDto;
import com.gooddaytaxi.payment.presentation.external.mapper.command.requestFefund.RefundRequestCreateMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.requestFefund.RefundRequestCreateResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/refund/requests")
@RequiredArgsConstructor
public class RefundRequestController {
    private final RefundRequestService requestService;

    @PostMapping
    public ResponseEntity<ApiResponse<RefundReqeustCreateResponseDto>> createRefundRequest(@RequestBody @Valid RefundRequestCreateRequestDto requestDto,
                                                                                     @RequestParam String role) {
        RefundRequestCreateCommand command = RefundRequestCreateMapper.toRequestCommand(requestDto);
        RefundRequestCreateResult result = requestService.createRefundRequest(command, role);
        RefundReqeustCreateResponseDto responseDto = RefundRequestCreateResponseMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }
}
