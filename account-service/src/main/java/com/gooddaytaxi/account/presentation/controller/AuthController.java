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

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        log.info("[회원가입 진입] email={}, role={}, name={}", request.getEmail(), request.getRole(), request.getName());

        try {
            UserSignupCommand command = UserSignupCommand.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .slackId(request.getSlackId())
                .role(request.toUserRoleOrThrow())
                .vehicleNumber(request.getVehicleNumber())
                .vehicleType(request.getVehicleType())
                .vehicleColor(request.getVehicleColor())
                .build();

            log.info("[회원가입 커맨드 생성 완료] email={}, role={}", command.getEmail(), command.getRole());

            String userId = registerUserUseCase.execute(command);

            log.info("[회원가입 완료] userId={}", userId);

            SignupResponse response = SignupResponse.of(userId);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "회원가입이 완료되었습니다."));

        } catch (Exception e) {
            log.error("[회원가입 실패] 이유: {}", e.getMessage(), e);  // 전체 스택트레이스 출력
            throw e;  // 전역 예외 핸들러 있으면 이걸로 처리
        }
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