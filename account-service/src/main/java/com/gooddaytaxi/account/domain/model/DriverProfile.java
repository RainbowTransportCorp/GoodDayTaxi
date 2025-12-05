package com.gooddaytaxi.account.domain.model;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 기사 프로필 엔티티 - 기사 전용 정보 (차량 정보 등)
 */
@Entity
@Table(name = "p_driver_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DriverProfile extends BaseEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "vehicle_number", nullable = false, unique = true, length = 20)
    private String vehicleNumber;

    @Column(name = "vehicle_type", nullable = false, length = 50)
    private String vehicleType;

    @Column(name = "vehicle_color", nullable = false, length = 30)
    private String vehicleColor;

    @Column(name = "online_status", nullable = false, length = 20)
    private String onlineStatus = "OFFLINE";

    @Column(name = "slack_user_id", length = 50)
    private String slackUserId;

    /**
     * 기사 프로필 엔티티 생성
     *
     * @param user 연결된 사용자 엔티티
     * @param vehicleNumber 차량 번호
     * @param vehicleType 차량 유형
     * @param vehicleColor 차량 색상
     */
    @Builder
    public DriverProfile(User user, String vehicleNumber, String vehicleType, String vehicleColor, String slackUserId) {
        this.user = user;
        this.userId = user.getUserUuid();
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.vehicleColor = vehicleColor;
        this.onlineStatus = "OFFLINE";
        this.slackUserId = slackUserId;
    }

    /**
     * 차량 정보 수정
     *
     * @param vehicleNumber 새로운 차량 번호
     * @param vehicleType 새로운 차량 유형
     * @param vehicleColor 새로운 차량 색상
     */
    public void updateVehicleInfo(String vehicleNumber, String vehicleType, String vehicleColor) {
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.vehicleColor = vehicleColor;
    }

    /**
     * 온라인 상태 변경
     *
     * @param status 새로운 상태값 (ONLINE/OFFLINE)
     */
    public void changeOnlineStatus(String status) {
        this.onlineStatus = status;
    }

    /**
     * 기사 온라인 상태로 변경
     */
    public void goOnline() {
        this.onlineStatus = "ONLINE";
    }

    /**
     * 기사 오프라인 상태로 변경
     */
    public void goOffline() {
        this.onlineStatus = "OFFLINE";
    }

    /**
     * 온라인 상태인지 확인
     *
     * @return 온라인 상태면 true, 아니면 false
     */
    public boolean isOnline() {
        return "ONLINE".equals(this.onlineStatus);
    }

    /**
     * 슬랙 사용자 ID 업데이트
     *
     * @param slackUserId 새로운 슬랙 사용자 ID
     */
    public void updateSlackUserId(String slackUserId) {
        this.slackUserId = slackUserId;
    }
}