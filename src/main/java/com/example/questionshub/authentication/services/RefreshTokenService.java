package com.example.questionshub.authentication.services;

import com.example.questionshub.authentication.repository.RefreshTokenRepository;
import com.example.questionshub.exceptions.NotFoundException;
import com.example.questionshub.exceptions.TokenRefreshException;
import com.example.questionshub.authentication.models.RefreshTokenEntity;
import com.example.questionshub.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${refreshTokenDurationMs}")
    private  Long refreshTokenDurationMs;

    @Value("${refreshTokenCookieName}")
    private String refreshTokenCookieName;

    @Value("${jwtCookieName}")
    private String jwtCookieName;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserService userService;

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

    public RefreshTokenEntity findByRefreshTokenOrElseThrow(String refreshToken) {
        return refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(
                        () -> new NotFoundException("Refresh token with token " + refreshToken + "was not found")
                );
    }

    public void verifyExpiration(RefreshTokenEntity refreshToken) {
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new TokenRefreshException(refreshToken.getToken(), "Refresh token was expired. Please make a new signin request");
        }
    }

    public RefreshTokenEntity generateRefreshToken(UUID id) {
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setUser(userService.findUserEntityByIdOrElseThrow(id));
        refreshTokenEntity.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshTokenEntity.setToken(UUID.randomUUID().toString());
        refreshTokenEntity = refreshTokenRepository.save(refreshTokenEntity);

        return refreshTokenEntity;
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

    @Transactional
    public void deleteRefreshTokenByUserIdOrElseThrow(UUID id) {
        refreshTokenRepository.deleteByUser(
                userService
                        .findUserEntityByIdOrElseThrow(id)
        );
    }
}
