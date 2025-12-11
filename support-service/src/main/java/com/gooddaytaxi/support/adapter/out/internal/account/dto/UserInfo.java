package com.gooddaytaxi.support.adapter.out.internal.account.dto;

import java.util.UUID;

public record UserInfo(

  UUID userId,
  String name,
  String email,
  String phoneNumber,
  UserRole role,
  UserStatus status,
  String slackUserId,
  String onlineStatus
) {}
