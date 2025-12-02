package com.gooddaytaxi.account.domain.repository;

import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.model.UserStatus;

import java.util.Optional;
import java.util.UUID;

/**
 * 사용자 조회 전용 리포지토리
 */
public interface UserReadRepository {
    
    /**
     * 사용자 UUID로 사용자 조회
     *
     * @param userUuid 조회할 사용자 UUID
     * @return 사용자 정보, 존재하지 않으면 empty Optional
     */
    Optional<User> findByUserUuid(UUID userUuid);
    
    /**
     * 사용자 ID로 사용자 조회
     *
     * @param userId 조회할 사용자 ID
     * @return 사용자 정보, 존재하지 않으면 empty Optional
     */
    Optional<User> findById(UUID userId);
    
    /**
     * 이메일로 사용자 조회
     *
     * @param email 조회할 이메일 주소
     * @return 사용자 정보, 존재하지 않으면 empty Optional
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 이메일 존재 여부 확인
     *
     * @param email 확인할 이메일 주소
     * @return 이메일이 존재하면 true, 아니면 false
     */
    boolean existsByEmail(String email);
    
    /**
     * 특정 상태인 사용자만 이메일로 조회
     *
     * @param email 조회할 이메일 주소
     * @param status 확인할 사용자 상태
     * @return 해당 상태의 사용자 정보, 존재하지 않으면 empty Optional
     */
    Optional<User> findByEmailAndStatus(String email, UserStatus status);
    
    /**
     * 특정 상태인 사용자 중 이메일 존재 여부 확인
     *
     * @param email 확인할 이메일 주소
     * @param status 확인할 사용자 상태
     * @return 해당 상태에서 이메일이 존재하면 true, 아니면 false
     */
    boolean existsByEmailAndStatus(String email, UserStatus status);
    
    /**
     * 삭제되지 않은 사용자를 이메일로 조회
     *
     * @param email 조회할 이메일 주소
     * @return 삭제되지 않은 사용자 정보, 존재하지 않으면 empty Optional
     */
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
}