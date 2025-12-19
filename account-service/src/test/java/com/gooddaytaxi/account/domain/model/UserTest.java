package com.gooddaytaxi.account.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * User 엔티티 도메인 로직 테스트
 * - 사용자 생성, 프로필 수정, 상태 변경, 권한 확인 등 핵심 비즈니스 로직 검증
 */
@DisplayName("User 엔티티 테스트")
class UserTest {

    private User user;
    
    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@test.com")
                .password("password")
                .name("테스트사용자")
                .phoneNumber("010-1234-5678")
                .slackId("U123456")
                .role(UserRole.PASSENGER)
                .build();
    }

    @Nested
    @DisplayName("사용자 생성")
    class UserCreation {
        
        @Test
        @DisplayName("승객 사용자 생성 성공")
        void createPassengerUser_Success() {
            // 승객 역할의 사용자 엔티티 생성 및 기본값 검증
            // given & when
            User passenger = User.builder()
                    .email("passenger@test.com")
                    .password("encodedPassword")
                    .name("승객")
                    .phoneNumber("010-1111-1111")
                    .slackId("U111111")
                    .role(UserRole.PASSENGER)
                    .build();
            
            // then
            assertThat(passenger.getEmail()).isEqualTo("passenger@test.com");
            assertThat(passenger.getName()).isEqualTo("승객");
            assertThat(passenger.getRole()).isEqualTo(UserRole.PASSENGER);
            assertThat(passenger.getStatus()).isEqualTo(UserStatus.ACTIVE); // 기본값
            assertThat(passenger.getSlackId()).isEqualTo("U111111");
        }
        
        @Test
        @DisplayName("기사 사용자 생성 성공")
        void createDriverUser_Success() {
            // 기사 역할의 사용자 엔티티 생성 및 슬랙ID 설정 검증
            // given & when
            User driver = User.builder()
                    .email("driver@test.com")
                    .password("encodedPassword")
                    .name("기사")
                    .phoneNumber("010-2222-2222")
                    .slackId("U222222")
                    .role(UserRole.DRIVER)
                    .build();
            
            // then
            assertThat(driver.getRole()).isEqualTo(UserRole.DRIVER);
            assertThat(driver.getSlackId()).isEqualTo("U222222");
        }
        
        @Test
        @DisplayName("일반관리자 생성 성공 - 슬랙ID 없음")
        void createAdminUser_WithoutSlackId_Success() {
            // 일반 관리자는 슬랙ID 없이 생성 가능 검증
            // given & when
            User admin = User.builder()
                    .email("admin@test.com")
                    .password("encodedPassword")
                    .name("관리자")
                    .phoneNumber("010-3333-3333")
                    .role(UserRole.ADMIN)
                    .build();
            
            // then
            assertThat(admin.getRole()).isEqualTo(UserRole.ADMIN);
            assertThat(admin.getSlackId()).isNull();
        }
        
        @Test
        @DisplayName("최고관리자 생성 성공 - 슬랙ID 포함")
        void createMasterAdminUser_WithSlackId_Success() {
            // 최고 관리자 역할의 사용자 생성 및 슬랙ID 필수 검증
            // given & when
            User masterAdmin = User.builder()
                    .email("master@test.com")
                    .password("encodedPassword")
                    .name("최고관리자")
                    .phoneNumber("010-4444-4444")
                    .slackId("U444444")
                    .role(UserRole.MASTER_ADMIN)
                    .build();
            
            // then
            assertThat(masterAdmin.getRole()).isEqualTo(UserRole.MASTER_ADMIN);
            assertThat(masterAdmin.getSlackId()).isEqualTo("U444444");
        }
    }

    @Nested
    @DisplayName("관리자 권한 확인")
    class AdminPermissionCheck {
        
        @Test
        @DisplayName("ADMIN 역할 사용자는 관리자로 인식")
        void isAdmin_WithAdminRole_ReturnsTrue() {
            // given
            User admin = User.builder()
                    .email("admin@test.com")
                    .password("password")
                    .name("관리자")
                    .phoneNumber("010-1111-1111")
                    .role(UserRole.ADMIN)
                    .build();
            
            // when & then
            assertThat(admin.isAdmin()).isTrue();
        }
        
        @Test
        @DisplayName("MASTER_ADMIN 역할 사용자는 관리자로 인식")
        void isAdmin_WithMasterAdminRole_ReturnsTrue() {
            // given
            User masterAdmin = User.builder()
                    .email("master@test.com")
                    .password("password")
                    .name("최고관리자")
                    .phoneNumber("010-2222-2222")
                    .role(UserRole.MASTER_ADMIN)
                    .build();
            
            // when & then
            assertThat(masterAdmin.isAdmin()).isTrue();
        }
        
        @Test
        @DisplayName("PASSENGER 역할 사용자는 관리자로 인식되지 않음")
        void isAdmin_WithPassengerRole_ReturnsFalse() {
            // given
            User passenger = User.builder()
                    .email("passenger@test.com")
                    .password("password")
                    .name("승객")
                    .phoneNumber("010-3333-3333")
                    .role(UserRole.PASSENGER)
                    .build();
            
            // when & then
            assertThat(passenger.isAdmin()).isFalse();
        }
        
        @Test
        @DisplayName("DRIVER 역할 사용자는 관리자로 인식되지 않음")
        void isAdmin_WithDriverRole_ReturnsFalse() {
            // given
            User driver = User.builder()
                    .email("driver@test.com")
                    .password("password")
                    .name("기사")
                    .phoneNumber("010-4444-4444")
                    .role(UserRole.DRIVER)
                    .build();
            
            // when & then
            assertThat(driver.isAdmin()).isFalse();
        }
    }

    @Nested
    @DisplayName("사용자 정보 수정")
    class UserProfileUpdate {
        
        @Test
        @DisplayName("프로필 정보 수정 성공")
        void updateProfile_Success() {
            // given
            String newName = "수정된이름";
            String newPhoneNumber = "010-9999-9999";
            
            // when
            user.updateProfile(newName, newPhoneNumber);
            
            // then
            assertThat(user.getName()).isEqualTo(newName);
            assertThat(user.getPhoneNumber()).isEqualTo(newPhoneNumber);
            // 다른 정보는 변경되지 않음
            assertThat(user.getEmail()).isEqualTo("test@test.com");
            assertThat(user.getRole()).isEqualTo(UserRole.PASSENGER);
        }
    }

    @Nested
    @DisplayName("사용자 상태 관리")
    class UserStatusManagement {
        
        @Test
        @DisplayName("사용자 비활성화")
        void deactivateUser_Success() {
            // given
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
            
            // when
            user.deactivate();
            
            // then
            assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
        }
        
        @Test
        @DisplayName("사용자 상태 직접 변경")
        void changeStatus_Success() {
            // given
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
            
            // when
            user.changeStatus(UserStatus.INACTIVE);
            
            // then
            assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("사용자 삭제 처리")
    class UserDeletion {
        
        @Test
        @DisplayName("사용자 논리삭제 처리")
        void markAsDeleted_Success() {
            // given
            String deletedBy = "admin@test.com";
            assertThat(user.getDeletedAt()).isNull();
            assertThat(user.getDeletedBy()).isNull();
            
            // when
            user.softDelete(deletedBy);
            
            // then
            assertThat(user.getDeletedAt()).isNotNull();
            assertThat(user.getDeletedBy()).isEqualTo(deletedBy);
            assertThat(user.getStatus()).isEqualTo(UserStatus.DELETED);
        }
    }

    @Nested
    @DisplayName("비즈니스 로직 검증")
    class BusinessLogicValidation {
        
        @Test
        @DisplayName("삭제된 사용자인지 확인")
        void isDeleted_WhenDeleted_ReturnsTrue() {
            // given
            user.softDelete("admin@test.com");
            
            // when & then
            assertThat(user.isDeleted()).isTrue();
        }
        
        @Test
        @DisplayName("활성 사용자인지 확인")
        void isDeleted_WhenActive_ReturnsFalse() {
            // when & then
            assertThat(user.isDeleted()).isFalse();
        }
        
        @Test
        @DisplayName("사용자 활성 상태 확인")
        void isActive_WhenActive_ReturnsTrue() {
            // when & then
            assertThat(user.isActive()).isTrue();
        }
        
        @Test
        @DisplayName("비활성 사용자 상태 확인")
        void isActive_WhenInactive_ReturnsFalse() {
            // given
            user.deactivate();
            
            // when & then
            assertThat(user.isActive()).isFalse();
        }
    }
}