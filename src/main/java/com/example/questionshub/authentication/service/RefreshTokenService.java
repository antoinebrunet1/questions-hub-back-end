package com.example.questionshub.authentication.service;

import com.example.questionshub.authentication.repository.RefreshTokenRepository;
import com.example.questionshub.exceptions.NotFoundException;
import com.example.questionshub.authentication.models.RefreshTokenEntity;
import com.example.questionshub.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${refreshTokenDurationMs}")
    private  Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserService userService;

    public RefreshTokenEntity findByRefreshTokenOrElseThrow(String refreshToken) {
        return refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(
                        () -> new NotFoundException("Refresh token with token " + refreshToken + "was not found")
                );
    }

    public RefreshTokenEntity generateRefreshToken(UUID id) {
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setUser(userService.findUserEntityByIdOrElseThrow(id));
        refreshTokenEntity.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshTokenEntity.setToken(UUID.randomUUID().toString());
        refreshTokenEntity = refreshTokenRepository.save(refreshTokenEntity);

        return refreshTokenEntity;
    }

    @Transactional
    public void deleteRefreshTokenByUserIdOrElseThrow(UUID id) {
        refreshTokenRepository.deleteByUser(
                userService
                        .findUserEntityByIdOrElseThrow(id)
        );
    }
}
