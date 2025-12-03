package com.gooddaytaxi.payment.presentation.external.mapper.response;

import com.gooddaytaxi.payment.application.result.AttemptReadResult;
import com.gooddaytaxi.payment.application.result.PaymentReadResult;
import com.gooddaytaxi.payment.presentation.external.dto.response.AttemptReadResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.PaymentReadResponseDto;

import java.util.Objects;

public class PaymentReadResponseMapper {
    public static PaymentReadResponseDto toResponse(PaymentReadResult result) {
        if (Objects.isNull(result.attemptResult())) {
            return new PaymentReadResponseDto(
                    result.paymentId(),
                    result.amount(),
                    result.status(),
                    result.method(),
                    result.passengerId(),
                    result.driverId(),
                    result.tripId(),
                    null
                    );
        }
        return new PaymentReadResponseDto(
                result.paymentId(),
                result.amount(),
                result.status(),
                result.method(),
                result.passengerId(),
                result.driverId(),
                result.tripId(),
                toAttemptResponse(result.attemptResult())
        );
    }

    private static AttemptReadResponseDto toAttemptResponse(AttemptReadResult result) {
        return new AttemptReadResponseDto(
                result.status(),
                result.pgMethod(),
                result.pgProvider(),
                result.approvedAt(),
                result.failDetail()
        );
    }
}
