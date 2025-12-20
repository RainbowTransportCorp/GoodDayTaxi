package com.gooddaytaxi.account.application.usecase;

import com.gooddaytaxi.account.application.dto.LoginResult;
import com.gooddaytaxi.account.application.dto.UserLoginCommand;
import com.gooddaytaxi.account.domain.exception.AccountBusinessException;
import com.gooddaytaxi.account.domain.exception.AccountErrorCode;
import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.model.UserRole;
import com.gooddaytaxi.account.domain.repository.UserRepository;
import com.gooddaytaxi.account.domain.service.JwtTokenProvider;
import com.gooddaytaxi.account.domain.service.PasswordEncoder;
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
 * LoginUserUseCase 애플리케이션 서비스 테스트
 * - 사용자 로그인 처리, JWT 토큰 생성, 인증 실패 케이스 검증
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LoginUserUseCase 테스트")
class LoginUserUseCaseTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    
    @InjectMocks
    private LoginUserUseCase loginUserUseCase;
    
    @BeforeEach
    void setUp() {
        // Setup은 개별 테스트에서 처리
    }

    @Nested
    @DisplayName("로그인 성공 케이스")
    class LoginSuccessCase {
        
        @Test
        @DisplayName("유효한 이메일과 비밀번호로 로그인 성공 - 승객")
        void login_WithValidCredentials_Passenger_Success() {
            // 승객 사용자의 정상적인 로그인 처리 및 JWT 토큰 생성 검증
            // given
            UserLoginCommand loginCommand = new UserLoginCommand("test@test.com", "rawPassword");
            String accessToken = "access-token";
            String refreshToken = "refresh-token";
            
            UUID userId = UUID.randomUUID();
            User mockUser = mock(User.class);
            given(mockUser.getPassword()).willReturn("encodedPassword");
            given(mockUser.getUserUuid()).willReturn(userId);
            given(mockUser.getRole()).willReturn(UserRole.PASSENGER);
            
            given(userRepository.findByEmailAndDeletedAtIsNull(loginCommand.getEmail()))
                    .willReturn(Optional.of(mockUser));
            given(passwordEncoder.matches(loginCommand.getPassword(), mockUser.getPassword()))
                    .willReturn(true);
            given(jwtTokenProvider.generateAccessToken(mockUser)).willReturn(accessToken);
            given(jwtTokenProvider.generateRefreshToken(mockUser)).willReturn(refreshToken);
            
            // when
            LoginResult result = loginUserUseCase.execute(loginCommand);
            
            // then
            assertThat(result.getAccessToken()).isEqualTo(accessToken);
            assertThat(result.getRefreshToken()).isEqualTo(refreshToken);
            assertThat(result.getUserUuid()).isEqualTo(userId.toString());
            assertThat(result.getRole()).isEqualTo(UserRole.PASSENGER.name());
            
            verify(userRepository).findByEmailAndDeletedAtIsNull(loginCommand.getEmail());
            verify(passwordEncoder).matches(loginCommand.getPassword(), mockUser.getPassword());
            verify(jwtTokenProvider).generateAccessToken(mockUser);
            verify(jwtTokenProvider).generateRefreshToken(mockUser);
        }
        
        @Test
        @DisplayName("유효한 이메일과 비밀번호로 로그인 성공 - 기사")
        void login_WithValidCredentials_Driver_Success() {
            // given
            UserLoginCommand driverCommand = new UserLoginCommand("driver@test.com", "rawPassword");
            String accessToken = "driver-access-token";
            String refreshToken = "driver-refresh-token";
            
            UUID userId = UUID.randomUUID();
            User mockDriverUser = mock(User.class);
            given(mockDriverUser.getPassword()).willReturn("encodedPassword");
            given(mockDriverUser.getUserUuid()).willReturn(userId);
            given(mockDriverUser.getRole()).willReturn(UserRole.DRIVER);
            
            given(userRepository.findByEmailAndDeletedAtIsNull(driverCommand.getEmail()))
                    .willReturn(Optional.of(mockDriverUser));
            given(passwordEncoder.matches(driverCommand.getPassword(), mockDriverUser.getPassword()))
                    .willReturn(true);
            given(jwtTokenProvider.generateAccessToken(mockDriverUser)).willReturn(accessToken);
            given(jwtTokenProvider.generateRefreshToken(mockDriverUser)).willReturn(refreshToken);
            
            // when
            LoginResult result = loginUserUseCase.execute(driverCommand);
            
            // then
            assertThat(result.getRole()).isEqualTo(UserRole.DRIVER.name());
            assertThat(result.getAccessToken()).isEqualTo(accessToken);
        }
        
        @Test
        @DisplayName("유효한 이메일과 비밀번호로 로그인 성공 - 관리자")
        void login_WithValidCredentials_Admin_Success() {
            // given
            UserLoginCommand adminCommand = new UserLoginCommand("admin@test.com", "rawPassword");
            String accessToken = "admin-access-token";
            String refreshToken = "admin-refresh-token";
            
            UUID userId = UUID.randomUUID();
            User mockAdminUser = mock(User.class);
            given(mockAdminUser.getPassword()).willReturn("encodedPassword");
            given(mockAdminUser.getUserUuid()).willReturn(userId);
            given(mockAdminUser.getRole()).willReturn(UserRole.ADMIN);
            
            given(userRepository.findByEmailAndDeletedAtIsNull(adminCommand.getEmail()))
                    .willReturn(Optional.of(mockAdminUser));
            given(passwordEncoder.matches(adminCommand.getPassword(), mockAdminUser.getPassword()))
                    .willReturn(true);
            given(jwtTokenProvider.generateAccessToken(mockAdminUser)).willReturn(accessToken);
            given(jwtTokenProvider.generateRefreshToken(mockAdminUser)).willReturn(refreshToken);
            
            // when
            LoginResult result = loginUserUseCase.execute(adminCommand);
            
            // then
            assertThat(result.getRole()).isEqualTo(UserRole.ADMIN.name());
            assertThat(result.getAccessToken()).isEqualTo(accessToken);
        }
        
        @Test
        @DisplayName("유효한 이메일과 비밀번호로 로그인 성공 - 최고관리자")
        void login_WithValidCredentials_MasterAdmin_Success() {
            // given
            UserLoginCommand masterCommand = new UserLoginCommand("master@test.com", "rawPassword");
            String accessToken = "master-access-token";
            String refreshToken = "master-refresh-token";
            
            UUID userId = UUID.randomUUID();
            User mockMasterAdminUser = mock(User.class);
            given(mockMasterAdminUser.getPassword()).willReturn("encodedPassword");
            given(mockMasterAdminUser.getUserUuid()).willReturn(userId);
            given(mockMasterAdminUser.getRole()).willReturn(UserRole.MASTER_ADMIN);
            
            given(userRepository.findByEmailAndDeletedAtIsNull(masterCommand.getEmail()))
                    .willReturn(Optional.of(mockMasterAdminUser));
            given(passwordEncoder.matches(masterCommand.getPassword(), mockMasterAdminUser.getPassword()))
                    .willReturn(true);
            given(jwtTokenProvider.generateAccessToken(mockMasterAdminUser)).willReturn(accessToken);
            given(jwtTokenProvider.generateRefreshToken(mockMasterAdminUser)).willReturn(refreshToken);
            
            // when
            LoginResult result = loginUserUseCase.execute(masterCommand);
            
            // then
            assertThat(result.getRole()).isEqualTo(UserRole.MASTER_ADMIN.name());
            assertThat(result.getAccessToken()).isEqualTo(accessToken);
        }
    }

    @Nested
    @DisplayName("로그인 실패 케이스")
    class LoginFailureCase {
        
        @Test
        @DisplayName("존재하지 않는 이메일로 로그인 시 예외 발생")
        void login_WithNonExistentEmail_ThrowsException() {
            // given
            UserLoginCommand nonExistentCommand = new UserLoginCommand("nonexistent@test.com", "password");
                    
            given(userRepository.findByEmailAndDeletedAtIsNull(nonExistentCommand.getEmail()))
                    .willReturn(Optional.empty());
            
            // when & then
            assertThatThrownBy(() -> loginUserUseCase.execute(nonExistentCommand))
                    .isInstanceOf(AccountBusinessException.class)
                    .hasMessage(AccountErrorCode.INVALID_CREDENTIALS.getMessage());
                    
            verify(userRepository).findByEmailAndDeletedAtIsNull(nonExistentCommand.getEmail());
            verify(passwordEncoder, never()).matches(any(), any());
            verify(jwtTokenProvider, never()).generateAccessToken(any());
            verify(jwtTokenProvider, never()).generateRefreshToken(any());
        }
        
        @Test
        @DisplayName("잘못된 비밀번호로 로그인 시 예외 발생")
        void login_WithWrongPassword_ThrowsException() {
            // given
            UserLoginCommand wrongPasswordCommand = new UserLoginCommand("test@test.com", "wrongPassword");
            
            User mockUser = mock(User.class);
            given(mockUser.getPassword()).willReturn("encodedPassword");
            
            given(userRepository.findByEmailAndDeletedAtIsNull(wrongPasswordCommand.getEmail()))
                    .willReturn(Optional.of(mockUser));
            given(passwordEncoder.matches(wrongPasswordCommand.getPassword(), mockUser.getPassword()))
                    .willReturn(false);
            
            // when & then
            assertThatThrownBy(() -> loginUserUseCase.execute(wrongPasswordCommand))
                    .isInstanceOf(AccountBusinessException.class)
                    .hasMessage(AccountErrorCode.INVALID_CREDENTIALS.getMessage());
                    
            verify(userRepository).findByEmailAndDeletedAtIsNull(wrongPasswordCommand.getEmail());
            verify(passwordEncoder).matches(wrongPasswordCommand.getPassword(), mockUser.getPassword());
            verify(jwtTokenProvider, never()).generateAccessToken(any());
            verify(jwtTokenProvider, never()).generateRefreshToken(any());
        }
        
        @Test
        @DisplayName("삭제된 사용자로 로그인 시 예외 발생")
        void login_WithDeletedUser_ThrowsException() {
            // given
            UserLoginCommand loginCommand = new UserLoginCommand("deleted@test.com", "password");
            
            given(userRepository.findByEmailAndDeletedAtIsNull(loginCommand.getEmail()))
                    .willReturn(Optional.empty());
            
            // when & then
            assertThatThrownBy(() -> loginUserUseCase.execute(loginCommand))
                    .isInstanceOf(AccountBusinessException.class)
                    .hasMessage(AccountErrorCode.INVALID_CREDENTIALS.getMessage());
                    
            verify(userRepository).findByEmailAndDeletedAtIsNull(loginCommand.getEmail());
            verify(passwordEncoder, never()).matches(any(), any());
        }
    }

    @Nested
    @DisplayName("JWT 토큰 생성")
    class JwtTokenGeneration {
        
        @Test
        @DisplayName("JWT 토큰 생성 서비스 호출 확인")
        void login_CallsJwtTokenGeneration_Success() {
            // given
            UserLoginCommand loginCommand = new UserLoginCommand("test@test.com", "rawPassword");
            String accessToken = "generated-access-token";
            String refreshToken = "generated-refresh-token";
            
            User mockUser = mock(User.class);
            given(mockUser.getPassword()).willReturn("encodedPassword");
            given(mockUser.getUserUuid()).willReturn(UUID.randomUUID());
            given(mockUser.getRole()).willReturn(UserRole.PASSENGER);
            
            given(userRepository.findByEmailAndDeletedAtIsNull(loginCommand.getEmail()))
                    .willReturn(Optional.of(mockUser));
            given(passwordEncoder.matches(loginCommand.getPassword(), mockUser.getPassword()))
                    .willReturn(true);
            given(jwtTokenProvider.generateAccessToken(mockUser)).willReturn(accessToken);
            given(jwtTokenProvider.generateRefreshToken(mockUser)).willReturn(refreshToken);
            
            // when
            LoginResult result = loginUserUseCase.execute(loginCommand);
            
            // then
            verify(jwtTokenProvider, times(1)).generateAccessToken(mockUser);
            verify(jwtTokenProvider, times(1)).generateRefreshToken(mockUser);
            
            assertThat(result.getAccessToken()).isEqualTo(accessToken);
            assertThat(result.getRefreshToken()).isEqualTo(refreshToken);
        }
    }

    @Nested
    @DisplayName("비즈니스 로직 검증")
    class BusinessLogicValidation {
        
        @Test
        @DisplayName("로그인 성공 시 사용자 정보가 올바르게 반환됨")
        void login_ReturnsCorrectUserInfo_Success() {
            // given
            UserLoginCommand specificCommand = new UserLoginCommand("specific@test.com", "rawPassword");
            
            UUID specificUserId = UUID.randomUUID();
            User mockSpecificUser = mock(User.class);
            given(mockSpecificUser.getPassword()).willReturn("encodedPassword");
            given(mockSpecificUser.getUserUuid()).willReturn(specificUserId);
            given(mockSpecificUser.getRole()).willReturn(UserRole.PASSENGER);
                    
            given(userRepository.findByEmailAndDeletedAtIsNull(specificCommand.getEmail()))
                    .willReturn(Optional.of(mockSpecificUser));
            given(passwordEncoder.matches(specificCommand.getPassword(), mockSpecificUser.getPassword()))
                    .willReturn(true);
            given(jwtTokenProvider.generateAccessToken(mockSpecificUser)).willReturn("access");
            given(jwtTokenProvider.generateRefreshToken(mockSpecificUser)).willReturn("refresh");
            
            // when
            LoginResult result = loginUserUseCase.execute(specificCommand);
            
            // then
            assertThat(result.getUserUuid()).isEqualTo(specificUserId.toString());
            assertThat(result.getRole()).isEqualTo(UserRole.PASSENGER.name());
        }
        
        @Test
        @DisplayName("로그인 과정에서 올바른 순서로 검증이 수행됨")
        void login_ValidatesInCorrectOrder_Success() {
            // given
            UserLoginCommand loginCommand = new UserLoginCommand("test@test.com", "rawPassword");
            
            User mockUser = mock(User.class);
            given(mockUser.getPassword()).willReturn("encodedPassword");
            given(mockUser.getUserUuid()).willReturn(UUID.randomUUID());
            given(mockUser.getRole()).willReturn(UserRole.PASSENGER);
            
            given(userRepository.findByEmailAndDeletedAtIsNull(loginCommand.getEmail()))
                    .willReturn(Optional.of(mockUser));
            given(passwordEncoder.matches(loginCommand.getPassword(), mockUser.getPassword()))
                    .willReturn(true);
            given(jwtTokenProvider.generateAccessToken(mockUser)).willReturn("access");
            given(jwtTokenProvider.generateRefreshToken(mockUser)).willReturn("refresh");
            
            // when
            loginUserUseCase.execute(loginCommand);
            
            // then - 검증 순서 확인
            var ordered = inOrder(userRepository, passwordEncoder, jwtTokenProvider);
            ordered.verify(userRepository).findByEmailAndDeletedAtIsNull(loginCommand.getEmail());
            ordered.verify(passwordEncoder).matches(loginCommand.getPassword(), mockUser.getPassword());
            ordered.verify(jwtTokenProvider).generateAccessToken(mockUser);
            ordered.verify(jwtTokenProvider).generateRefreshToken(mockUser);
        }
    }
}