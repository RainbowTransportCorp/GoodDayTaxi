package com.gooddaytaxi.payment.application.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/*
* validator 이외의 공통으로 쓰는 메서드 정리한 서비스
* */

public class CommonService {
    //pageable 생성
    public static Pageable toPageable (boolean sortAscending, int page, int size, String sortBy) {
        //오름차순/내림차순
        Sort.Direction direction = sortAscending ? Sort.Direction.ASC : Sort.Direction.DESC;
        //데이터 조회
        return PageRequest.of(page-1, size, Sort.by(direction, sortBy));
    }

}
