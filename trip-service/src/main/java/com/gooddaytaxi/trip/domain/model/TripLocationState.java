package com.gooddaytaxi.trip.domain.model;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_trip_location_state")
@Getter
@NoArgsConstructor
public class TripLocationState extends BaseEntity {
    @Id
    @Column(name = "trip_id", nullable = false)
    private UUID tripId;

    @Column(name = "last_region", nullable = false, length = 100)
    private String lastRegion;

    @Column(name = "last_sequence", nullable = false)
    private Long lastSequence;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static TripLocationState init(UUID tripId, String region) {
        TripLocationState e = new TripLocationState();
        e.tripId = tripId;
        e.lastRegion = region;
        e.lastSequence = 0L;
        e.updatedAt = LocalDateTime.now();
        return e;
    }

    public void update(String newRegion, long newSeq) {
        this.lastRegion = newRegion;
        this.lastSequence = newSeq;
        this.updatedAt = LocalDateTime.now();
    }
}
