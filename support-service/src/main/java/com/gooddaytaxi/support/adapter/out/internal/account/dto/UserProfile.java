package com.gooddaytaxi.support.adapter.out.internal.account.dto;

import com.gooddaytaxi.support.application.service.UserRole;

import java.util.UUID;

/**
* User 정보 조회 DTO
*/
public record UserProfile(

  UUID userId,
  String name,
  String email,
  String phoneNumber,
  UserRole role,
  UserStatus status,
  String slackUserId,
  String onlineStatus
) {}
