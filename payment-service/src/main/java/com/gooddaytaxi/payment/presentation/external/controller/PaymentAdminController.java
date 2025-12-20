package com.gooddaytaxi.payment.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.payment.application.command.payment.PaymentSearchCommand;
import com.gooddaytaxi.payment.application.command.refundRequest.RefundReqeustSearchCommand;
import com.gooddaytaxi.payment.application.command.refundRequest.RefundRequestResponseCreateCommand;
import com.gooddaytaxi.payment.application.result.payment.PaymentAdminReadResult;
import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestAdminReadResult;
import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestCreateResult;
import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestReadResult;
import com.gooddaytaxi.payment.application.service.PaymentService;
import com.gooddaytaxi.payment.application.service.RefundRequestService;
import com.gooddaytaxi.payment.presentation.external.dto.request.payment.PaymentAdminSearchRequestDto;
import com.gooddaytaxi.payment.presentation.external.dto.request.requestRefund.RefundRequestAdminSearchRequestDto;
import com.gooddaytaxi.payment.presentation.external.dto.request.requestRefund.RefundRequestResponseRequestDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.payment.PaymentAdminReadResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.reqeustRufund.RefundReqeustCreateResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.reqeustRufund.RefundRequestAdminReadResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.reqeustRufund.RefundRequestReadResponseDto;
import com.gooddaytaxi.payment.presentation.external.mapper.command.payment.PaymentSearchMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.command.requestFefund.RefundRequestCreateMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.command.requestFefund.RefundRequestSearchMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.payment.PaymentReadResponseMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.requestFefund.RefundRequestCreateResponseMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.requestFefund.RefundRequestReadResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/*
* 결제, 환불 요청, 환불 관리자용 controller
* 환불요청 단건조회는
* */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/payments")
public class PaymentAdminController {
    private final PaymentService paymentService;
    private final RefundRequestService requestService;

    //결제 단건조회
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentAdminReadResponseDto>> getPayment(@PathVariable UUID paymentId,
                                                                               @RequestHeader(value = "X-User-Role") String role) {
        PaymentAdminReadResult result = paymentService.getAdminPayment(paymentId, role);
        PaymentAdminReadResponseDto responseDto = PaymentReadResponseMapper.toAdminResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    //결제 검색 기능
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PaymentAdminReadResponseDto>>> searchPayment(@RequestBody @Valid PaymentAdminSearchRequestDto requestDto,
                                                                                        @RequestHeader(value = "X-User-Role") String role) {
        PaymentSearchCommand command = PaymentSearchMapper.toAdminCommand(requestDto);
        Page<PaymentAdminReadResult> result = paymentService.searchAdminPayment(command, role);
        Page<PaymentAdminReadResponseDto> responseDto = PaymentReadResponseMapper.toPageAdminResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    //환불 요청 단건 조회 - 관리자용
    @GetMapping("/refund/requests/{requestId}")
    public ResponseEntity<ApiResponse<RefundRequestAdminReadResponseDto>> getRefundRequest(@PathVariable UUID requestId,

                                                                                      @RequestHeader(value = "X-User-Role") String role) {
        RefundRequestAdminReadResult result = requestService.getAdminRefundRequest(requestId, role);
        RefundRequestAdminReadResponseDto responseDto = RefundRequestReadResponseMapper.toAdminResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    //환불 요청 검색
    @GetMapping("/refund/requests/search")
    public ResponseEntity<ApiResponse<Page<RefundRequestReadResponseDto>>> searchRefundRequests(@RequestBody @Valid RefundRequestAdminSearchRequestDto requestDto,
                                                                                                @RequestHeader(value = "X-User-Role") String role) {
        RefundReqeustSearchCommand command = RefundRequestSearchMapper.toAdminCommand(requestDto);
        Page<RefundRequestReadResult> results = requestService.searchAdminRefundRequests(command, role);
        Page<RefundRequestReadResponseDto> responseDtos = RefundRequestReadResponseMapper.toPageResponse(results);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDtos));
    }
    //환불 요청 응답
    @PostMapping("/refund/requests/response")
    public ResponseEntity<ApiResponse<RefundReqeustCreateResponseDto>> respondToRefundRequest(@RequestBody @Valid RefundRequestResponseRequestDto requestDto,
                                                                                              @RequestHeader(value = "X-User-UUID") UUID userId,
                                                                                              @RequestHeader(value = "X-User-Role") String role) {
        RefundRequestResponseCreateCommand command = RefundRequestCreateMapper.toResponseCommand(requestDto);
        RefundRequestCreateResult result = requestService.respondToRefundRequest(command, userId, role);
        RefundReqeustCreateResponseDto responseDto = RefundRequestCreateResponseMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }



}
