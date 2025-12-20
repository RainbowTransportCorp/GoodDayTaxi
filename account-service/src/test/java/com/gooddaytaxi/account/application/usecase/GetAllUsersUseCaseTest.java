package com.gooddaytaxi.account.application.usecase;

import com.gooddaytaxi.account.application.dto.AdminUserListResponse;
import com.gooddaytaxi.account.application.mapper.AdminUserListMapper;
import com.gooddaytaxi.account.domain.exception.AccountBusinessException;
import com.gooddaytaxi.account.domain.exception.AccountErrorCode;
import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.model.UserRole;
import com.gooddaytaxi.account.domain.model.UserStatus;
import com.gooddaytaxi.account.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

/**
 * GetAllUsersUseCase 애플리케이션 서비스 테스트
 * - 관리자 권한 검증, 전체 사용자 목록 조회 기능 검증
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GetAllUsersUseCase 테스트")
class GetAllUsersUseCaseTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private AdminUserListMapper mapper;
    
    @InjectMocks
    private GetAllUsersUseCase getAllUsersUseCase;
    
    private User user1;
    private User user2;
    private AdminUserListResponse response1;
    private AdminUserListResponse response2;
    
    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .email("user1@test.com")
                .password("password")
                .name("User1")
                .phoneNumber("010-1111-1111")
                .role(UserRole.PASSENGER)
                .build();
                
        user2 = User.builder()
                .email("user2@test.com")
                .password("password")
                .name("User2")
                .phoneNumber("010-2222-2222")
                .role(UserRole.DRIVER)
                .build();
                
        response1 = AdminUserListResponse.builder()
                .name("User1")
                .email("user1@test.com")
                .role("PASSENGER")
                .status("ACTIVE")
                .build();
                
        response2 = AdminUserListResponse.builder()
                .name("User2")
                .email("user2@test.com")
                .role("DRIVER")
                .status("ACTIVE")
                .build();
    }

    @Nested
    @DisplayName("정상 케이스")
    class SuccessCase {
        
        @Test
        @DisplayName("ADMIN 권한으로 전체 사용자 조회 성공")
        void getUsers_WithAdminRole_Success() {
            // given
            String requestUserRole = "ADMIN";
            List<User> users = Arrays.asList(user1, user2);
            List<AdminUserListResponse> expectedResponses = Arrays.asList(response1, response2);
            
            given(userRepository.findAll()).willReturn(users);
            given(mapper.toResponseList(users)).willReturn(expectedResponses);
            
            // when
            List<AdminUserListResponse> result = getAllUsersUseCase.execute(requestUserRole);
            
            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(response1, response2);
            
            verify(userRepository).findAll();
            verify(mapper).toResponseList(users);
        }
        
        @Test
        @DisplayName("MASTER_ADMIN 권한으로 전체 사용자 조회 성공")
        void getUsers_WithMasterAdminRole_Success() {
            // given
            String requestUserRole = "MASTER_ADMIN";
            List<User> users = Arrays.asList(user1);
            List<AdminUserListResponse> expectedResponses = Arrays.asList(response1);
            
            given(userRepository.findAll()).willReturn(users);
            given(mapper.toResponseList(users)).willReturn(expectedResponses);
            
            // when
            List<AdminUserListResponse> result = getAllUsersUseCase.execute(requestUserRole);
            
            // then
            assertThat(result).hasSize(1);
            assertThat(result).containsExactly(response1);
            
            verify(userRepository).findAll();
            verify(mapper).toResponseList(users);
        }
    }

    @Nested
    @DisplayName("예외 케이스")
    class ExceptionCase {
        
        @Test
        @DisplayName("권한 없는 역할로 접근 시 예외 발생")
        void getUsers_WithInvalidRole_ThrowsException() {
            // given
            String invalidRole = "PASSENGER";
            
            // when & then
            assertThatThrownBy(() -> getAllUsersUseCase.execute(invalidRole))
                    .isInstanceOf(AccountBusinessException.class)
                    .hasMessage(AccountErrorCode.ACCESS_DENIED.getMessage());
                    
            verify(userRepository, never()).findAll();
            verify(mapper, never()).toResponseList(any());
        }
        
        @Test
        @DisplayName("DRIVER 역할로 접근 시 예외 발생")
        void getUsers_WithDriverRole_ThrowsException() {
            // given
            String driverRole = "DRIVER";
            
            // when & then
            assertThatThrownBy(() -> getAllUsersUseCase.execute(driverRole))
                    .isInstanceOf(AccountBusinessException.class)
                    .hasMessage(AccountErrorCode.ACCESS_DENIED.getMessage());
        }
        
        @Test
        @DisplayName("null 역할로 접근 시 예외 발생")
        void getUsers_WithNullRole_ThrowsException() {
            // given
            String nullRole = null;
            
            // when & then
            assertThatThrownBy(() -> getAllUsersUseCase.execute(nullRole))
                    .isInstanceOf(AccountBusinessException.class)
                    .hasMessage(AccountErrorCode.ACCESS_DENIED.getMessage());
        }
    }
}