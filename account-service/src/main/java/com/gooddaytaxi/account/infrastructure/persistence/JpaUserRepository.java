package com.gooddaytaxi.account.infrastructure.persistence;

import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.model.UserStatus;
import com.gooddaytaxi.account.domain.repository.UserReadRepository;
import com.gooddaytaxi.account.domain.repository.UserWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA 기반 사용자 리포지토리 구현체
 */
@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserReadRepository, UserWriteRepository {

    private final UserJpaRepository userJpaRepository;

    // UserReadRepository 구현
    @Override
    public Optional<User> findById(String userId) {
        return userJpaRepository.findById(userId);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByEmailAndStatus(String email, UserStatus status) {
        return userJpaRepository.findByEmailAndStatus(email, status);
    }

    @Override
    public boolean existsByEmailAndStatus(String email, UserStatus status) {
        return userJpaRepository.existsByEmailAndStatus(email, status);
    }

    @Override
    public Optional<User> findByEmailAndDeletedAtIsNull(String email) {
        return userJpaRepository.findByEmailAndDeletedAtIsNull(email);
    }

    // UserWriteRepository 구현
    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public void delete(User user) {
        userJpaRepository.delete(user);
    }
}