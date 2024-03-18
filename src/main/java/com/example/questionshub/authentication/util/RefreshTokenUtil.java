package com.example.questionshub.authentication.util;

import com.example.questionshub.authentication.models.RefreshTokenEntity;
import com.example.questionshub.authentication.repository.RefreshTokenRepository;
import com.example.questionshub.exceptions.TokenRefreshException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.time.Instant;

@Service
public class RefreshTokenUtil {
    @Value("${refreshTokenCookieName}")
    private String refreshTokenCookieName;

    @Value("${jwtCookieName}")
    private String jwtCookieName;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public String getRefreshTokenFromCookies(HttpServletRequest request) throws BadRequestException {
        String refreshToken = getCookieValueByName(request, refreshTokenCookieName);

        if ((refreshToken == null) || (refreshToken.isEmpty())) {
            throw new BadRequestException("Refresh Token is empty!");
        }

        return refreshToken;
    }

    private String getCookieValueByName(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);

        if (cookie != null) {
            return cookie.getValue();
        }

        return null;
    }

    public void verifyExpiration(RefreshTokenEntity refreshToken) {
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new TokenRefreshException(refreshToken.getToken(), "Refresh token was expired. Please make a new signin request");
        }
    }

    public ResponseCookie getJwtCookie(String jwt) {
        return generateCookieForRefreshTokenPath(jwtCookieName, jwt);
    }

    public ResponseCookie getRefreshTokenCookie(String refreshToken) {
        return generateCookieForRefreshTokenPath(refreshTokenCookieName, refreshToken);
    }

    private ResponseCookie generateCookieForRefreshTokenPath(String name, String value) {
        return ResponseCookie
                .from(name, value)
                .path("/api/v1/auth")
                .maxAge(24 * 60 * 60)
                .httpOnly(true)
                .build();
    }
}
