package com.gooddaytaxi.account.presentation.controller;

import com.gooddaytaxi.account.application.dto.LoginResult;
import com.gooddaytaxi.account.application.dto.RefreshTokenCommand;
import com.gooddaytaxi.account.application.dto.RefreshTokenResult;
import com.gooddaytaxi.account.application.dto.UserLoginCommand;
import com.gooddaytaxi.account.application.dto.UserSignupCommand;
import com.gooddaytaxi.account.application.usecase.LoginUserUseCase;
import com.gooddaytaxi.account.application.usecase.RefreshTokenUseCase;
import com.gooddaytaxi.account.application.usecase.RegisterUserUseCase;
import com.gooddaytaxi.account.presentation.dto.LoginRequest;
import com.gooddaytaxi.account.presentation.dto.LoginResponse;
import com.gooddaytaxi.account.presentation.dto.RefreshTokenRequest;
import com.gooddaytaxi.account.presentation.dto.RefreshTokenResponse;
import com.gooddaytaxi.account.presentation.dto.SignupRequest;
import com.gooddaytaxi.account.presentation.dto.SignupResponse;
import com.gooddaytaxi.common.core.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 관련 REST API 컨트롤러
 */
@Tag(name = "인증", description = "회원가입, 로그인, 토큰 재발급 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;

    @Operation(summary = "회원가입", description = "새 사용자(승객/기사/관리자)를 등록합니다. 기사: 차량정보+슬랙ID 필수, 승객: 슬랙ID 필수, 일반관리자(ADMIN): 슬랙ID 불필요, 최고관리자(MASTER_ADMIN): 슬랙ID 필수")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        UserSignupCommand command = UserSignupCommand.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .slackId(request.getSlackId())
                .role(request.getRole())
                .vehicleNumber(request.getVehicleNumber())
                .vehicleType(request.getVehicleType())
                .vehicleColor(request.getVehicleColor())
                .build();

        String userId = registerUserUseCase.execute(command);
        SignupResponse response = SignupResponse.of(userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "회원가입이 완료되었습니다."));
    }

    @Operation(summary = "로그인", description = "모든 사용자(승객/기사/관리자)가 이메일과 비밀번호로 로그인하여 JWT 토큰(액세스+리프레시)을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        UserLoginCommand command = new UserLoginCommand(request.getEmail(), request.getPassword());
        
        LoginResult result = loginUserUseCase.execute(command);
        LoginResponse response = LoginResponse.of(result.getAccessToken(), result.getRefreshToken(), result.getUserUuid(), result.getRole(), result.getEmail());
        
        return ResponseEntity.ok(ApiResponse.success(response, "로그인이 완료되었습니다."));
    }

    @Operation(summary = "토큰 재발급", description = "모든 사용자가 리프레시 토큰을 사용해 새로운 액세스 토큰과 리프레시 토큰을 발급받습니다. 토큰 만료 시 사용합니다.")
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenCommand command = new RefreshTokenCommand(request.getRefreshToken());
        
        RefreshTokenResult result = refreshTokenUseCase.execute(command);
        RefreshTokenResponse response = RefreshTokenResponse.of(result.getAccessToken(), result.getRefreshToken());
        
        return ResponseEntity.ok(ApiResponse.success(response, "토큰이 재발급되었습니다."));
    }
}