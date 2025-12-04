package com.gooddaytaxi.account.application.mapper;

import com.gooddaytaxi.account.application.dto.ChangeUserStatusResponse;
import com.gooddaytaxi.account.domain.model.User;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class ChangeUserStatusMapper {
    
    public ChangeUserStatusResponse toResponse(User user) {
        return ChangeUserStatusResponse.builder()
                .userId(user.getUserUuid())
                .status(user.getStatus().name())
                .message("사용자 상태 변경 완료")
                .build();
    }
}