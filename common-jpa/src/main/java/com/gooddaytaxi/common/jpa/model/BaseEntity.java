package com.gooddaytaxi.common.jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("생성 시간")
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    @Comment("생성자 ID")
    private Long createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    @Comment("수정 시간")
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    @Comment("수정자 ID")
    private Long updatedBy;
}
