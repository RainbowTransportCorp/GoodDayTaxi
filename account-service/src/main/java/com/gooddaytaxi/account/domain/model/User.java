package com.gooddaytaxi.account.domain.model;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 사용자 엔티티 - 승객과 기사의 기본 정보
 */
@Entity
@Table(name = "p_users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DriverProfile driverProfile;

    /**
     * 사용자 엔티티 생성
     *
     * @param email 사용자 이메일
     * @param password 암호화된 비밀번호
     * @param name 사용자 이름
     * @param phoneNumber 전화번호
     * @param role 사용자 역할 (PASSENGER/DRIVER/ADMIN)
     */
    @Builder
    public User(String email, String password, String name, String phoneNumber, UserRole role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.status = UserStatus.ACTIVE;
    }

    /**
     * 사용자 프로필 정보 수정
     *
     * @param name 새로운 이름
     * @param phoneNumber 새로운 전화번호
     */
    public void updateProfile(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    /**
     * 사용자 계정 비활성화
     */
    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

    /**
     * 사용자 계정 소프트 삭제
     *
     * @param deletedBy 삭제한 사용자 ID
     */
    public void softDelete(String deletedBy) {
        this.status = UserStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    /**
     * 사용자 UUID 조회
     *
     * @return 사용자 UUID
     */
    public UUID getUserUuid() {
        return this.userId;
    }

    /**
     * 활성 상태인지 확인
     *
     * @return 활성 상태면 true, 아니면 false
     */
    public boolean isActive() {
        return UserStatus.ACTIVE.equals(this.status);
    }

    /**
     * 삭제된 상태인지 확인
     *
     * @return 삭제된 상태면 true, 아니면 false
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    /**
     * 관리자 역할인지 확인
     *
     * @return 관리자인 경우 true, 아니면 false
     */
    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }

    /**
     * 기사 역할인지 확인
     *
     * @return 기사인 경우 true, 아니면 false
     */
    public boolean isDriver() {
        return this.role == UserRole.DRIVER;
    }

    /**
     * 승객 역할인지 확인
     *
     * @return 승객인 경우 true, 아니면 false
     */
    public boolean isPassenger() {
        return this.role == UserRole.PASSENGER;
    }

}