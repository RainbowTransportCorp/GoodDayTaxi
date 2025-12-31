package com.gooddaytaxi.infra.gateway.presentation;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        Long timestamp
) {
    /**
     * 성공 응답 생성 (데이터 포함)
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, data, System.currentTimeMillis());
    }

    /**
     * 성공 응답 생성 (데이터 + 메시지)
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, System.currentTimeMillis());
    }

    /**
     * 성공 응답 생성 (메시지만)
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, System.currentTimeMillis());
    }

    /**
     * 실패 응답 생성 (에러 데이터)
     */
    public static <T> ApiResponse<T> error(T errorData) {
        return new ApiResponse<>(false, null, errorData, System.currentTimeMillis());
    }
}