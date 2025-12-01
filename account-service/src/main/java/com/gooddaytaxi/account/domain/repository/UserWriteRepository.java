package com.gooddaytaxi.account.domain.repository;

import com.gooddaytaxi.account.domain.model.User;

/**
 * 사용자 생성/수정/삭제 전용 리포지토리
 */
public interface UserWriteRepository {
    
    /**
     * 사용자 정보 저장
     *
     * @param user 저장할 사용자 엔티티
     * @return 저장된 사용자 엔티티
     */
    User save(User user);
    
    /**
     * 사용자 정보 삭제
     *
     * @param user 삭제할 사용자 엔티티
     */
    void delete(User user);
}