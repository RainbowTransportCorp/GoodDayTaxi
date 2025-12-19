package com.gooddaytaxi.account.application.usecase;

import com.gooddaytaxi.account.application.dto.UserSignupCommand;
import com.gooddaytaxi.account.domain.exception.AccountBusinessException;
import com.gooddaytaxi.account.domain.exception.AccountErrorCode;
import com.gooddaytaxi.account.domain.model.DriverProfile;
import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.model.UserRole;
import com.gooddaytaxi.account.domain.repository.DriverProfileRepository;
import com.gooddaytaxi.account.domain.repository.UserWriteRepository;
import com.gooddaytaxi.account.domain.service.PasswordEncoder;
import com.gooddaytaxi.account.domain.service.UserValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

/**
 * RegisterUserUseCase 애플리케이션 서비스 테스트
 * - 역할별 회원가입 처리, 유효성 검증, 기사 프로필 생성 등 가입 로직 검증
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterUserUseCase 테스트")
class RegisterUserUseCaseTest {
    
    @Mock
    private UserWriteRepository userWriteRepository;
    
    @Mock
    private DriverProfileRepository driverProfileRepository;
    
    @Mock
    private UserValidationService userValidationService;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;
    
    @Nested
    @DisplayName("승객 회원가입")
    class PassengerSignup {
        
        @Test
        @DisplayName("승객 회원가입 성공")
        void registerPassenger_Success() {
            // given
            UserSignupCommand command = UserSignupCommand.builder()
                    .email("passenger@test.com")
                    .password("Password123!")
                    .name("승객")
                    .phoneNumber("010-1111-1111")
                    .slackId("U123456")
                    .role(UserRole.PASSENGER)
                    .build();
            
            UUID userId = UUID.randomUUID();
            User mockUser = mock(User.class);
            given(mockUser.getUserUuid()).willReturn(userId);
            
            given(passwordEncoder.encode(command.getPassword())).willReturn("encodedPassword");
            given(userWriteRepository.save(any(User.class))).willReturn(mockUser);
            
            // when
            String result = registerUserUseCase.execute(command);
            
            // then
            assertThat(result).isEqualTo(userId.toString());
            
            verify(userValidationService).validateSignupRequest(command);
            verify(passwordEncoder).encode(command.getPassword());
            verify(userWriteRepository).save(any(User.class));
            verify(driverProfileRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("기사 회원가입")
    class DriverSignup {
        
        @Test
        @DisplayName("기사 회원가입 성공 - 차량정보와 슬랙ID 포함")
        void registerDriver_WithVehicleInfo_Success() {
            // given
            UserSignupCommand command = UserSignupCommand.builder()
                    .email("driver@test.com")
                    .password("Password123!")
                    .name("기사")
                    .phoneNumber("010-2222-2222")
                    .slackId("U789012")
                    .role(UserRole.DRIVER)
                    .vehicleNumber("34나5678")
                    .vehicleType("그랜저")
                    .vehicleColor("검정")
                    .build();
            
            UUID userId = UUID.randomUUID();
            User mockUser = mock(User.class);
            given(mockUser.getUserUuid()).willReturn(userId);
            
            DriverProfile mockDriverProfile = mock(DriverProfile.class);
            
            given(passwordEncoder.encode(command.getPassword())).willReturn("encodedPassword");
            given(userWriteRepository.save(any(User.class))).willReturn(mockUser);
            given(driverProfileRepository.save(any(DriverProfile.class))).willReturn(mockDriverProfile);
            
            // when
            String result = registerUserUseCase.execute(command);
            
            // then
            assertThat(result).isEqualTo(userId.toString());
            
            verify(userValidationService).validateSignupRequest(command);
            verify(userWriteRepository).save(any(User.class));
            verify(driverProfileRepository).save(any(DriverProfile.class));
        }
    }

    @Nested
    @DisplayName("관리자 회원가입")
    class AdminSignup {
        
        @Test
        @DisplayName("일반관리자(ADMIN) 회원가입 성공 - 슬랙ID 불필요")
        void registerAdmin_WithoutSlackId_Success() {
            // given
            UserSignupCommand command = UserSignupCommand.builder()
                    .email("admin@test.com")
                    .password("Password123!")
                    .name("관리자")
                    .phoneNumber("010-3333-3333")
                    .role(UserRole.ADMIN)
                    .build();
            
            UUID userId = UUID.randomUUID();
            User mockUser = mock(User.class);
            given(mockUser.getUserUuid()).willReturn(userId);
            
            given(passwordEncoder.encode(command.getPassword())).willReturn("encodedPassword");
            given(userWriteRepository.save(any(User.class))).willReturn(mockUser);
            
            // when
            String result = registerUserUseCase.execute(command);
            
            // then
            assertThat(result).isEqualTo(userId.toString());
            
            verify(userValidationService).validateSignupRequest(command);
            verify(driverProfileRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("최고관리자(MASTER_ADMIN) 회원가입 성공 - 슬랙ID 필수")
        void registerMasterAdmin_WithSlackId_Success() {
            // given
            UserSignupCommand command = UserSignupCommand.builder()
                    .email("master@test.com")
                    .password("Password123!")
                    .name("최고관리자")
                    .phoneNumber("010-4444-4444")
                    .slackId("U999999")
                    .role(UserRole.MASTER_ADMIN)
                    .build();
            
            UUID userId = UUID.randomUUID();
            User mockUser = mock(User.class);
            given(mockUser.getUserUuid()).willReturn(userId);
            
            given(passwordEncoder.encode(command.getPassword())).willReturn("encodedPassword");
            given(userWriteRepository.save(any(User.class))).willReturn(mockUser);
            
            // when
            String result = registerUserUseCase.execute(command);
            
            // then
            assertThat(result).isEqualTo(userId.toString());
            
            verify(userValidationService).validateSignupRequest(command);
        }
    }

    @Nested
    @DisplayName("예외 케이스")
    class ExceptionCase {
        
        @Test
        @DisplayName("이메일 중복 시 예외 발생")
        void registerUser_WithDuplicateEmail_ThrowsException() {
            // given
            UserSignupCommand command = UserSignupCommand.builder()
                    .email("duplicate@test.com")
                    .password("Password123!")
                    .name("사용자")
                    .phoneNumber("010-5555-5555")
                    .role(UserRole.PASSENGER)
                    .build();
                    
            doThrow(new AccountBusinessException(AccountErrorCode.DUPLICATE_EMAIL))
                    .when(userValidationService).validateSignupRequest(command);
            
            // when & then
            assertThatThrownBy(() -> registerUserUseCase.execute(command))
                    .isInstanceOf(AccountBusinessException.class)
                    .hasMessage(AccountErrorCode.DUPLICATE_EMAIL.getMessage());
                    
            verify(userValidationService).validateSignupRequest(command);
            verify(userWriteRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("유효하지 않은 비밀번호로 회원가입 시 예외 발생")
        void registerUser_WithInvalidPassword_ThrowsException() {
            // given
            UserSignupCommand command = UserSignupCommand.builder()
                    .email("test@test.com")
                    .password("weak")
                    .name("사용자")
                    .phoneNumber("010-6666-6666")
                    .role(UserRole.PASSENGER)
                    .build();
            
            doThrow(new AccountBusinessException(AccountErrorCode.WEAK_PASSWORD))
                    .when(userValidationService).validateSignupRequest(command);
            
            // when & then
            assertThatThrownBy(() -> registerUserUseCase.execute(command))
                    .isInstanceOf(AccountBusinessException.class)
                    .hasMessage(AccountErrorCode.WEAK_PASSWORD.getMessage());
                    
            verify(userValidationService).validateSignupRequest(command);
            verify(userWriteRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("역할별 유효성 검증 실패 시 예외 발생")
        void registerUser_WithInvalidRoleData_ThrowsException() {
            // given
            UserSignupCommand command = UserSignupCommand.builder()
                    .email("test@test.com")
                    .password("Password123!")
                    .name("기사")
                    .phoneNumber("010-7777-7777")
                    .role(UserRole.DRIVER)
                    // 차량정보 누락
                    .build();
            
            doThrow(new AccountBusinessException(AccountErrorCode.MISSING_VEHICLE_INFO))
                    .when(userValidationService).validateSignupRequest(command);
            
            // when & then
            assertThatThrownBy(() -> registerUserUseCase.execute(command))
                    .isInstanceOf(AccountBusinessException.class)
                    .hasMessage(AccountErrorCode.MISSING_VEHICLE_INFO.getMessage());
                    
            verify(userValidationService).validateSignupRequest(command);
            verify(userWriteRepository, never()).save(any());
        }
    }
}