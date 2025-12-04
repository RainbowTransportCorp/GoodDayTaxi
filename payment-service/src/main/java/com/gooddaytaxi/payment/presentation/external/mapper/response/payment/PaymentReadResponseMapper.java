package com.gooddaytaxi.payment.presentation.external.mapper.response.payment;

import com.gooddaytaxi.payment.application.result.payment.AttemptReadResult;
import com.gooddaytaxi.payment.application.result.payment.PaymentReadResult;
import com.gooddaytaxi.payment.presentation.external.dto.response.payment.AttemptReadResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.payment.PaymentReadResponseDto;
import org.springframework.data.domain.Page;

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
                    null,
                    result.createdAt(),
                    result.updatedAt()
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
                result.approvedAt(),
                result.failDetail()
        );
    }

    public static Page<PaymentReadResponseDto> toPageResponse(Page<PaymentReadResult> result) {
        return result.map(PaymentReadResponseMapper::toResponse);
    }
}
