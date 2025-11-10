package com.ktb.ktb_community.common.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${jwt.refreshTokenValidityInMs}")
    private Long refreshTokenValidityInMs;

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
//                .secure(true)
                .path("/api/auth")
                .maxAge(refreshTokenValidityInMs)
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie clearRefreshTokenCookie() {
        return ResponseCookie.from("refreshToken", null)
                .httpOnly(true)
                .path("/api/auth")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }
}
