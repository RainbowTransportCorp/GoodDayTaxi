package com.gooddaytaxi.payment.presentation.external.mapper.response.payment;

import com.gooddaytaxi.payment.application.result.payment.AttemptReadResult;
import com.gooddaytaxi.payment.application.result.payment.PaymentAdminReadResult;
import com.gooddaytaxi.payment.application.result.payment.PaymentReadResult;
import com.gooddaytaxi.payment.presentation.external.dto.response.payment.AttemptReadResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.payment.PaymentAdminReadResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.payment.PaymentReadResponseDto;
import org.springframework.data.domain.Page;

import java.util.Objects;

public class PaymentReadResponseMapper {
    public static PaymentReadResponseDto toResponse(PaymentReadResult result) {
        return new PaymentReadResponseDto(
                result.paymentId(),
                result.amount(),
                result.status(),
                result.method(),
                result.approvedAt()
        );
    }
    public static PaymentAdminReadResponseDto toAdminResponse(PaymentAdminReadResult result) {
        if (Objects.isNull(result.attemptResult())) {
            return new PaymentAdminReadResponseDto(
                    result.paymentId(),
                    result.amount(),
                    result.status(),
                    result.method(),
                    result.passengerId(),
                    result.driverId(),
                    result.tripId(),
                    null,
                    result.createdAt(),
                    result.updatedAt()
            );
        }
        return new PaymentAdminReadResponseDto(
                result.paymentId(),
                result.amount(),
                result.status(),
                result.method(),
                result.passengerId(),
                result.driverId(),
                result.tripId(),
                toAttemptResponse(result.attemptResult()),
                result.createdAt(),
                result.updatedAt()
        );
    }

    private static AttemptReadResponseDto toAttemptResponse(AttemptReadResult result) {
        return new AttemptReadResponseDto(
                result.status(),
                result.pgMethod(),
                result.pgProvider(),
                result.pgApprovedAt(),
                result.failDetail()
        );
    }

    public static Page<PaymentAdminReadResponseDto> toPageResponse(Page<PaymentAdminReadResult> result) {
        return result.map(PaymentReadResponseMapper::toAdminResponse);
    }
}
