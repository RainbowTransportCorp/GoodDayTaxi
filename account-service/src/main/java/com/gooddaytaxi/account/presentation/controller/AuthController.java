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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 관련 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;

    /**
     * 사용자 회원가입 API
     *
     * @param request 회원가입 요청 데이터 (이메일, 비밀번호, 이름, 전화번호, 역할, 차량정보)
     * @return 201 Created - 생성된 사용자 ID 포함 응답
     * @throws BusinessException 이메일 중복, 차량정보 누락, 차량번호 중복 시 발생
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        UserSignupCommand command = UserSignupCommand.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
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

    /**
     * 사용자 로그인 API
     *
     * @param request 로그인 요청 데이터 (이메일, 비밀번호)
     * @return 200 OK - JWT 토큰과 사용자 정보 포함 응답
     * @throws BusinessException 인증 실패 시 발생
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        UserLoginCommand command = new UserLoginCommand(request.getEmail(), request.getPassword());
        
        LoginResult result = loginUserUseCase.execute(command);
        LoginResponse response = LoginResponse.of(result.getAccessToken(), result.getRefreshToken(), result.getUserUuid(), result.getRole());
        
        return ResponseEntity.ok(ApiResponse.success(response, "로그인이 완료되었습니다."));
    }

    /**
     * 토큰 재발급 API
     *
     * @param request 리프레시 토큰 요청 데이터
     * @return 200 OK - 새로운 Access Token과 Refresh Token 포함 응답
     * @throws BusinessException 토큰이 유효하지 않거나 만료된 경우 발생
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenCommand command = new RefreshTokenCommand(request.getRefreshToken());
        
        RefreshTokenResult result = refreshTokenUseCase.execute(command);
        RefreshTokenResponse response = RefreshTokenResponse.of(result.getAccessToken(), result.getRefreshToken());
        
        return ResponseEntity.ok(ApiResponse.success(response, "토큰이 재발급되었습니다."));
    }
}