package com.gooddaytaxi.account.application.usecase;

import com.gooddaytaxi.account.application.dto.ChangeUserStatusCommand;
import com.gooddaytaxi.account.application.dto.ChangeUserStatusResponse;
import com.gooddaytaxi.account.application.mapper.ChangeUserStatusMapper;
import com.gooddaytaxi.account.domain.exception.AccountBusinessException;
import com.gooddaytaxi.account.domain.exception.AccountErrorCode;
import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.model.UserRole;
import com.gooddaytaxi.account.domain.model.UserStatus;
import com.gooddaytaxi.account.domain.repository.UserRepository;
import com.gooddaytaxi.account.domain.repository.UserWriteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

/**
 * ChangeUserStatusUseCase 애플리케이션 서비스 테스트
 * - MASTER_ADMIN 전용 권한 검증, 사용자 상태 변경 기능 검증
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ChangeUserStatusUseCase 테스트")
class ChangeUserStatusUseCaseTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ChangeUserStatusMapper mapper;
    
    @InjectMocks
    private ChangeUserStatusUseCase changeUserStatusUseCase;
    
    private UUID userId;
    private User user;
    private ChangeUserStatusCommand command;
    private ChangeUserStatusResponse expectedResponse;
    
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        
        user = User.builder()
                .email("user@test.com")
                .password("password")
                .name("TestUser")
                .phoneNumber("010-1234-5678")
                .role(UserRole.PASSENGER)
                .build();
                
        command = ChangeUserStatusCommand.builder()
                .status("INACTIVE")
                .build();
                
        expectedResponse = ChangeUserStatusResponse.builder()
                .userId(userId)
                .status("INACTIVE")
                .message("사용자 상태 변경 완료")
                .build();
    }

    @Nested
    @DisplayName("정상 케이스")
    class SuccessCase {
        
        @Test
        @DisplayName("MASTER_ADMIN이 사용자 상태를 INACTIVE로 변경")
        void changeStatus_MasterAdminToInactive_Success() {
            // given
            String requestUserRole = "MASTER_ADMIN";
            
            given(userRepository.findByUserUuid(userId)).willReturn(Optional.of(user));
            given(userRepository.save(any(User.class))).willReturn(user);
            given(mapper.toResponse(user)).willReturn(expectedResponse);
            
            // when
            ChangeUserStatusResponse result = changeUserStatusUseCase.execute(requestUserRole, userId, command);
            
            // then
            assertThat(result).isEqualTo(expectedResponse);
            assertThat(result.getStatus()).isEqualTo("INACTIVE");
            
            verify(userRepository).findByUserUuid(userId);
            verify(userRepository).save(user);
            verify(mapper).toResponse(user);
        }
        
        @Test
        @DisplayName("MASTER_ADMIN이 사용자 상태를 ACTIVE로 변경")
        void changeStatus_MasterAdminToActive_Success() {
            // given
            String requestUserRole = "MASTER_ADMIN";
            ChangeUserStatusCommand activeCommand = ChangeUserStatusCommand.builder()
                    .status("ACTIVE")
                    .build();
                    
            ChangeUserStatusResponse activeResponse = ChangeUserStatusResponse.builder()
                    .userId(userId)
                    .status("ACTIVE")
                    .message("사용자 상태 변경 완료")
                    .build();
            
            given(userRepository.findByUserUuid(userId)).willReturn(Optional.of(user));
            given(userRepository.save(any(User.class))).willReturn(user);
            given(mapper.toResponse(user)).willReturn(activeResponse);
            
            // when
            ChangeUserStatusResponse result = changeUserStatusUseCase.execute(requestUserRole, userId, activeCommand);
            
            // then
            assertThat(result).isEqualTo(activeResponse);
            assertThat(result.getStatus()).isEqualTo("ACTIVE");
        }
    }

    @Nested
    @DisplayName("권한 예외 케이스")
    class PermissionExceptionCase {
        
        @Test
        @DisplayName("ADMIN 권한으로 접근 시 예외 발생 - MASTER_ADMIN만 가능")
        void changeStatus_WithAdminRole_ThrowsException() {
            // given
            String adminRole = "ADMIN";
            
            // when & then
            assertThatThrownBy(() -> changeUserStatusUseCase.execute(adminRole, userId, command))
                    .isInstanceOf(AccountBusinessException.class)
                    .hasMessage(AccountErrorCode.ACCESS_DENIED.getMessage());
                    
            verify(userRepository, never()).findByUserUuid(any());
            verify(userRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("PASSENGER 권한으로 접근 시 예외 발생")
        void changeStatus_WithPassengerRole_ThrowsException() {
            // given
            String passengerRole = "PASSENGER";
            
            // when & then
            assertThatThrownBy(() -> changeUserStatusUseCase.execute(passengerRole, userId, command))
                    .isInstanceOf(AccountBusinessException.class)
                    .hasMessage(AccountErrorCode.ACCESS_DENIED.getMessage());
        }
        
        @Test
        @DisplayName("DRIVER 권한으로 접근 시 예외 발생")
        void changeStatus_WithDriverRole_ThrowsException() {
            // given
            String driverRole = "DRIVER";
            
            // when & then
            assertThatThrownBy(() -> changeUserStatusUseCase.execute(driverRole, userId, command))
                    .isInstanceOf(AccountBusinessException.class)
                    .hasMessage(AccountErrorCode.ACCESS_DENIED.getMessage());
        }
    }

    @Nested
    @DisplayName("사용자 예외 케이스")
    class UserExceptionCase {
        
        @Test
        @DisplayName("존재하지 않는 사용자 ID로 접근 시 예외 발생")
        void changeStatus_WithNonExistentUser_ThrowsException() {
            // given
            String requestUserRole = "MASTER_ADMIN";
            UUID nonExistentUserId = UUID.randomUUID();
            
            given(userRepository.findByUserUuid(nonExistentUserId)).willReturn(Optional.empty());
            
            // when & then
            assertThatThrownBy(() -> changeUserStatusUseCase.execute(requestUserRole, nonExistentUserId, command))
                    .isInstanceOf(AccountBusinessException.class)
                    .hasMessage(AccountErrorCode.USER_NOT_FOUND.getMessage());
                    
            verify(userRepository).findByUserUuid(nonExistentUserId);
            verify(userRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("이미 삭제된 사용자에 대한 상태 변경 시 예외 발생")
        void changeStatus_WithDeletedUser_ThrowsException() {
            // given
            String requestUserRole = "MASTER_ADMIN";
            
            given(userRepository.findByUserUuid(userId)).willReturn(Optional.empty());
            
            // when & then
            assertThatThrownBy(() -> changeUserStatusUseCase.execute(requestUserRole, userId, command))
                    .isInstanceOf(AccountBusinessException.class)
                    .hasMessage(AccountErrorCode.USER_NOT_FOUND.getMessage());
        }
    }
}