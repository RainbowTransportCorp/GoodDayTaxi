package com.gooddaytaxi.payment.presentation.external.dto.request.refund;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.util.UUID;

public record RefundCreateRequestDto(@NotNull String reason,
                                     @Pattern(
                                             regexp = "^\\d{4}년 \\d{2}월 \\d{2}일 \\d{2}시 \\d{2}분$",
                                             message = "incidentAt은 'yyyy년 MM월 dd일 HH시 mm분' 형식이어야 합니다."
                                     )
                                     @NotNull String incidentAt,
                                     @Pattern(
                                             regexp = "^[^|]*문제$",
                                             message = "incidentSummary는 '~문제'로 끝나야 합니다."
                                     )
                                     @NotNull String incidentSummary,
                                     LocalDateTime executedAt,
                                     UUID requestId) {
}
