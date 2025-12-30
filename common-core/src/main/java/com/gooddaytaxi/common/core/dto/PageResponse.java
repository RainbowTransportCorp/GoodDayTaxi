package com.gooddaytaxi.common.core.dto;

import java.util.List;

/**
 * Spring Page를 그대로 JSON으로 반환하면
 * 내부의 Sort / Pageable 객체까지 직렬화 대상이 되어
 * HttpMessageNotWritableException이 발생할 수 있다.
 *
 * PageResponse는 실제 응답에 필요한 값만 복사하여
 * 직렬화 가능한 형태로 잘라내기 위해 사용한다.
 */
public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean last
) {}
