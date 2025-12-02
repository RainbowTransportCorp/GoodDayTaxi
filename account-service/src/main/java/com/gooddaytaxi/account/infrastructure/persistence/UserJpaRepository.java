package com.gooddaytaxi.account.infrastructure.persistence;

import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * 사용자 Spring Data JPA 리포지토리
 */
public interface UserJpaRepository extends JpaRepository<User, UUID> {
    
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<User> findByEmailAndStatus(String email, UserStatus status);
    
    boolean existsByEmailAndStatus(String email, UserStatus status);
    
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
}