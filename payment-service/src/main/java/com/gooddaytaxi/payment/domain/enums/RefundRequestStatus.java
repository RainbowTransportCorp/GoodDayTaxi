package com.gooddaytaxi.payment.domain.enums;

public enum RefundRequestStatus {
    REQUESTED,APPROVED,REJECTED,COMPLETED,CANCELED;

    public static RefundRequestStatus of (String status) {
        for (RefundRequestStatus s : RefundRequestStatus.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Invalid RefundRequestStatus: " + status);
    }
}
