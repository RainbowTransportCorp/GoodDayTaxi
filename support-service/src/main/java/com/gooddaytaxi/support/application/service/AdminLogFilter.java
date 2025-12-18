package com.gooddaytaxi.support.application.service;

/**
* 관리자의 전체 로그 조회를 위한 검색 필터 기준
*/
public record AdminLogFilter(
        String logType,
        // 날짜
        String from,
        String to
    ) {}

