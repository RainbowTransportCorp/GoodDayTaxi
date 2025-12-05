package com.gooddaytaxi.support.adapter.out.external.http.dto;

import java.util.UUID;

public record UserInfo(

  UUID userId,
  String username,
  String slackUserId,
  UserRole userRole,
  UserStatus userStatus
) {}
