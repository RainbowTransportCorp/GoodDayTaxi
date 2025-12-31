package com.gooddaytaxi.account.infrastructure.persistence;

import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.model.UserRole;
import com.gooddaytaxi.account.domain.model.UserStatus;
import com.gooddaytaxi.account.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    
    private final UserJpaRepository userJpaRepository;
    
    @Override
    public Optional<User> findByUserUuid(UUID userUuid) {
        return userJpaRepository.findById(userUuid);
    }
    
    @Override
    public Optional<User> findById(UUID userId) {
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
    
    @Override
    public Optional<User> findByUserUuidAndRoleAndDeletedAtIsNull(UUID userUuid, UserRole role) {
        return userJpaRepository.findByUserIdAndRoleAndDeletedAtIsNull(userUuid, role);
    }
    
    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll();
    }
    
    @Override
    public List<User> findByRoleAndStatusAndDeletedAtIsNull(UserRole role, UserStatus status) {
        return userJpaRepository.findByRoleAndStatusAndDeletedAtIsNull(role, status);
    }
    
    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }
    
    @Override
    public void delete(User user) {
        userJpaRepository.delete(user);
    }
    
    @Override
    public void deleteById(UUID userId) {
        userJpaRepository.deleteById(userId);
    }
}