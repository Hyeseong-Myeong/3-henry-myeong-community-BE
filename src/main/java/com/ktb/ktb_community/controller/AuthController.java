package com.ktb.ktb_community.controller;

import com.ktb.ktb_community.common.advice.ApiResponse;
import com.ktb.ktb_community.common.Security.CookieUtil;
import com.ktb.ktb_community.common.Security.LoginRequestDto;
import com.ktb.ktb_community.common.Security.LoginResponseDto;
import com.ktb.ktb_community.common.Security.LoginSuccessResponseDto;
import com.ktb.ktb_community.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService; // AuthService 주입
    private final CookieUtil cookieUtil;

    @PostMapping
    public ResponseEntity<ApiResponse<LoginSuccessResponseDto>> login(@RequestBody LoginRequestDto loginRequestDto){

        LoginResponseDto responseDto = authService.login(loginRequestDto);

        ResponseCookie refreshTokenCookie = cookieUtil.createRefreshTokenCookie(responseDto.getRefreshToken());

        ApiResponse<LoginSuccessResponseDto> successResponse = ApiResponse.success(
                "login_successful",
                new LoginSuccessResponseDto(responseDto.getAccessToken())
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(successResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginSuccessResponseDto>> refresh(
            @CookieValue("refreshToken") String refreshToken
    ){

        String accessToken =  authService.reissueAccessToken(refreshToken).getAccessToken();

        ApiResponse<LoginSuccessResponseDto> successResponse = ApiResponse.success(
                "refresh_successful",
                new LoginSuccessResponseDto(accessToken)
        );

        return ResponseEntity.ok(successResponse);
    }

    @DeleteMapping
    public ResponseEntity<Void> logout(Principal principal) {

        String userEmail = principal.getName();

        authService.logout(userEmail);
        ResponseCookie clearCookie = cookieUtil.clearRefreshTokenCookie();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .build();
    }

}
