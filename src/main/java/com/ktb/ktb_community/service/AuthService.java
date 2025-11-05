package com.ktb.ktb_community.service;

import com.ktb.ktb_community.common.Security.JwtProvider;
import com.ktb.ktb_community.common.Security.LoginRequestDto;
import com.ktb.ktb_community.common.Security.LoginResponseDto;
import com.ktb.ktb_community.common.Security.RefreshToken;
import com.ktb.ktb_community.entity.User;
import com.ktb.ktb_community.exception.NoPermissionException;
import com.ktb.ktb_community.exception.NotFoundException;
import com.ktb.ktb_community.repository.RefreshTokenRepository;
import com.ktb.ktb_community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //AT, RT 발급
    //RT redis 에 저장
    public LoginResponseDto login(LoginRequestDto loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new NoPermissionException("user", "email not found"));

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new NoPermissionException("password", "invalid or expired password");
        }

        String strUserId = user.getUserId().toString();

        String accessToken = jwtProvider.createAccessToken(strUserId);
        String refreshToken = jwtProvider.createRefreshToken(strUserId);

        refreshTokenRepository.save(new RefreshToken(user.getUserId(), refreshToken));

        return new LoginResponseDto(accessToken, refreshToken);
    }

    //redis 에서 RT 삭제
    //쿠키 무효화는 AuthController 에서 설정
    public void logout(String refreshToken) {

        RefreshToken savedRefreshToken = refreshTokenRepository.findByToken(refreshToken).orElseThrow(
                () -> new NoPermissionException("accessToken", "invalid or expired token"));

        refreshTokenRepository.delete(savedRefreshToken);
    }

    public LoginResponseDto reissueAccessToken(String refreshToken) {

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken).orElseThrow(
                () -> new NoPermissionException("refreshToken", "invalid or expired token")
        );

        String accessToken = jwtProvider.createAccessToken(token.getUserId().toString());

        //AT 만 재발급
        //RT 는 쿠키에 그대로 유지
        return new LoginResponseDto(accessToken, null);
    }
}
