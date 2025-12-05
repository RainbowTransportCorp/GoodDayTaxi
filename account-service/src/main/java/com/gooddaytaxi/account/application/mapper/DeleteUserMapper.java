package com.gooddaytaxi.account.application.mapper;

import com.gooddaytaxi.account.application.dto.DeleteUserResponse;
import com.gooddaytaxi.account.domain.model.User;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class DeleteUserMapper {
    
    public DeleteUserResponse toResponse(User user) {
        return DeleteUserResponse.builder()
                .userId(user.getUserUuid())
                .status(user.getStatus().name())
                .deletedAt(user.getDeletedAt())
                .message("사용자 삭제 처리 완료")
                .build();
    }
}