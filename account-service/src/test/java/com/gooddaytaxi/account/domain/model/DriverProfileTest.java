package com.gooddaytaxi.account.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * DriverProfile 엔티티 도메인 로직 테스트
 * - 기사 프로필 생성, 차량정보 수정, 온라인 상태 관리 등 기사 전용 기능 검증
 */
@DisplayName("DriverProfile 엔티티 테스트")
class DriverProfileTest {

    private DriverProfile driverProfile;
    private UUID userId;
    
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        driverProfile = DriverProfile.builder()
                .userId(userId)
                .vehicleNumber("12가3456")
                .vehicleType("소나타")
                .vehicleColor("흰색")
                .slackUserId("U123456")
                .build();
    }

    @Nested
    @DisplayName("기사 프로필 생성")
    class DriverProfileCreation {
        
        @Test
        @DisplayName("기사 프로필 생성 성공 - 슬랙ID 포함")
        void createDriverProfile_WithSlackId_Success() {
            // given & when
            DriverProfile profile = DriverProfile.builder()
                    .userId(userId)
                    .vehicleNumber("34나5678")
                    .vehicleType("그랜저")
                    .vehicleColor("검정")
                    .slackUserId("U789012")
                    .build();
            
            // then
            assertThat(profile.getUserId()).isEqualTo(userId);
            assertThat(profile.getVehicleNumber()).isEqualTo("34나5678");
            assertThat(profile.getVehicleType()).isEqualTo("그랜저");
            assertThat(profile.getVehicleColor()).isEqualTo("검정");
            assertThat(profile.getSlackUserId()).isEqualTo("U789012");
            assertThat(profile.getOnlineStatus()).isEqualTo("OFFLINE"); // 기본값
        }
        
        @Test
        @DisplayName("기사 프로필 생성 성공 - 슬랙ID 없음")
        void createDriverProfile_WithoutSlackId_Success() {
            // given & when
            DriverProfile profile = DriverProfile.builder()
                    .userId(userId)
                    .vehicleNumber("56다7890")
                    .vehicleType("아반떼")
                    .vehicleColor("파랑")
                    .build();
            
            // then
            assertThat(profile.getSlackUserId()).isNull();
            assertThat(profile.getOnlineStatus()).isEqualTo("OFFLINE"); // 기본값
        }
    }

    @Nested
    @DisplayName("차량 정보 수정")
    class VehicleInfoUpdate {
        
        @Test
        @DisplayName("차량 정보 수정 성공")
        void updateVehicleInfo_Success() {
            // given
            String newVehicleNumber = "78라9012";
            String newVehicleType = "카니발";
            String newVehicleColor = "은색";
            
            // when
            driverProfile.updateVehicleInfo(newVehicleNumber, newVehicleType, newVehicleColor);
            
            // then
            assertThat(driverProfile.getVehicleNumber()).isEqualTo(newVehicleNumber);
            assertThat(driverProfile.getVehicleType()).isEqualTo(newVehicleType);
            assertThat(driverProfile.getVehicleColor()).isEqualTo(newVehicleColor);
            // 다른 정보는 변경되지 않음
            assertThat(driverProfile.getUserId()).isEqualTo(userId);
            assertThat(driverProfile.getSlackUserId()).isEqualTo("U123456");
        }
        
        @Test
        @DisplayName("차량 번호만 변경")
        void updateVehicleNumber_Only_Success() {
            // given
            String originalType = driverProfile.getVehicleType();
            String originalColor = driverProfile.getVehicleColor();
            String newVehicleNumber = "99마1234";
            
            // when
            driverProfile.updateVehicleInfo(newVehicleNumber, originalType, originalColor);
            
            // then
            assertThat(driverProfile.getVehicleNumber()).isEqualTo(newVehicleNumber);
            assertThat(driverProfile.getVehicleType()).isEqualTo(originalType);
            assertThat(driverProfile.getVehicleColor()).isEqualTo(originalColor);
        }
    }

    @Nested
    @DisplayName("온라인 상태 관리")
    class OnlineStatusManagement {
        
        @Test
        @DisplayName("온라인 상태로 변경")
        void goOnline_Success() {
            // given
            assertThat(driverProfile.getOnlineStatus()).isEqualTo("OFFLINE");
            assertThat(driverProfile.isOnline()).isFalse();
            
            // when
            driverProfile.goOnline();
            
            // then
            assertThat(driverProfile.getOnlineStatus()).isEqualTo("ONLINE");
            assertThat(driverProfile.isOnline()).isTrue();
        }
        
        @Test
        @DisplayName("오프라인 상태로 변경")
        void goOffline_Success() {
            // given
            driverProfile.goOnline(); // 먼저 온라인으로 만들기
            assertThat(driverProfile.isOnline()).isTrue();
            
            // when
            driverProfile.goOffline();
            
            // then
            assertThat(driverProfile.getOnlineStatus()).isEqualTo("OFFLINE");
            assertThat(driverProfile.isOnline()).isFalse();
        }
        
        @Test
        @DisplayName("직접 상태 변경 - ONLINE")
        void changeOnlineStatus_ToOnline_Success() {
            // when
            driverProfile.changeOnlineStatus("ONLINE");
            
            // then
            assertThat(driverProfile.getOnlineStatus()).isEqualTo("ONLINE");
            assertThat(driverProfile.isOnline()).isTrue();
        }
        
        @Test
        @DisplayName("직접 상태 변경 - OFFLINE")
        void changeOnlineStatus_ToOffline_Success() {
            // given
            driverProfile.goOnline();
            
            // when
            driverProfile.changeOnlineStatus("OFFLINE");
            
            // then
            assertThat(driverProfile.getOnlineStatus()).isEqualTo("OFFLINE");
            assertThat(driverProfile.isOnline()).isFalse();
        }
    }

    @Nested
    @DisplayName("슬랙 사용자 ID 관리")
    class SlackUserIdManagement {
        
        @Test
        @DisplayName("슬랙 사용자 ID 업데이트")
        void updateSlackUserId_Success() {
            // given
            String newSlackUserId = "U999999";
            assertThat(driverProfile.getSlackUserId()).isEqualTo("U123456");
            
            // when
            driverProfile.updateSlackUserId(newSlackUserId);
            
            // then
            assertThat(driverProfile.getSlackUserId()).isEqualTo(newSlackUserId);
        }
        
        @Test
        @DisplayName("슬랙 사용자 ID를 null로 변경")
        void updateSlackUserId_ToNull_Success() {
            // given
            assertThat(driverProfile.getSlackUserId()).isNotNull();
            
            // when
            driverProfile.updateSlackUserId(null);
            
            // then
            assertThat(driverProfile.getSlackUserId()).isNull();
        }
        
        @Test
        @DisplayName("빈 문자열로 슬랙 사용자 ID 변경")
        void updateSlackUserId_ToEmpty_Success() {
            // when
            driverProfile.updateSlackUserId("");
            
            // then
            assertThat(driverProfile.getSlackUserId()).isEmpty();
        }
    }

    @Nested
    @DisplayName("상태 확인 메서드")
    class StatusCheckMethods {
        
        @Test
        @DisplayName("온라인 상태 확인 - ONLINE일 때")
        void isOnline_WhenOnline_ReturnsTrue() {
            // given
            driverProfile.goOnline();
            
            // when & then
            assertThat(driverProfile.isOnline()).isTrue();
        }
        
        @Test
        @DisplayName("온라인 상태 확인 - OFFLINE일 때")
        void isOnline_WhenOffline_ReturnsFalse() {
            // given
            driverProfile.goOffline();
            
            // when & then
            assertThat(driverProfile.isOnline()).isFalse();
        }
        
        @Test
        @DisplayName("온라인 상태 확인 - 기본값 OFFLINE")
        void isOnline_DefaultOffline_ReturnsFalse() {
            // given - 기본 생성 상태 (OFFLINE)
            
            // when & then
            assertThat(driverProfile.isOnline()).isFalse();
            assertThat(driverProfile.getOnlineStatus()).isEqualTo("OFFLINE");
        }
    }

    @Nested
    @DisplayName("복합 시나리오")
    class ComplexScenarios {
        
        @Test
        @DisplayName("차량 정보 수정 후 온라인 상태 변경")
        void updateVehicleInfo_ThenChangeOnlineStatus_Success() {
            // given
            String newVehicleNumber = "11바2233";
            String newVehicleType = "포터";
            String newVehicleColor = "노랑";
            
            // when
            driverProfile.updateVehicleInfo(newVehicleNumber, newVehicleType, newVehicleColor);
            driverProfile.goOnline();
            
            // then
            assertThat(driverProfile.getVehicleNumber()).isEqualTo(newVehicleNumber);
            assertThat(driverProfile.getVehicleType()).isEqualTo(newVehicleType);
            assertThat(driverProfile.getVehicleColor()).isEqualTo(newVehicleColor);
            assertThat(driverProfile.isOnline()).isTrue();
        }
        
        @Test
        @DisplayName("슬랙 ID 업데이트 후 상태 변경")
        void updateSlackId_ThenChangeStatus_Success() {
            // given
            String newSlackId = "U555555";
            
            // when
            driverProfile.updateSlackUserId(newSlackId);
            driverProfile.goOnline();
            
            // then
            assertThat(driverProfile.getSlackUserId()).isEqualTo(newSlackId);
            assertThat(driverProfile.isOnline()).isTrue();
        }
    }
}