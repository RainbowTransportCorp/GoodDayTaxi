package com.gooddaytaxi.support.adapter.out.internal.account.dto;

import java.util.UUID;

public record UserInfo(

  UUID userId,
  String username,
  String email,
  String slackUserId,
  UserRole userRole,
  UserStatus userStatus
) {}
