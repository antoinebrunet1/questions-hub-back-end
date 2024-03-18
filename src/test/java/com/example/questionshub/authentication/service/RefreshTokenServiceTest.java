package com.example.questionshub.authentication.service;

import com.example.questionshub.authentication.models.RefreshTokenEntity;
import com.example.questionshub.authentication.repository.RefreshTokenRepository;
import com.example.questionshub.user.entity.UserEntity;
import com.example.questionshub.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RefreshTokenService underTest;

    @Test
    void findByRefreshTokenOrElseThrowShouldCallFindByTokenInRepoWithCorrectRefreshToken() {
        // given
        String refreshToken = UUID.randomUUID().toString();
        Optional<RefreshTokenEntity> optionalQuestionEntity = Optional.of(new RefreshTokenEntity());
        // when
        when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(optionalQuestionEntity);
        underTest.findByRefreshTokenOrElseThrow(refreshToken);
        // then
        verify(refreshTokenRepository).findByToken(refreshToken);
    }

    @Test
    void generateRefreshTokenShouldCallSaveInRepo() {
        // given
        UUID id = UUID.randomUUID();
        UserEntity user = new UserEntity();
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setUser(user);
        // when
        when(userService.findUserEntityByIdOrElseThrow(id)).thenReturn(user);
        when(refreshTokenRepository.save(any(RefreshTokenEntity.class))).thenReturn(refreshTokenEntity);
        ReflectionTestUtils.setField(underTest, "refreshTokenDurationMs", 0L);
        underTest.generateRefreshToken(id);
        // then
        verify(refreshTokenRepository).save(any(RefreshTokenEntity.class));
    }

    @Test
    void deleteRefreshTokenByUserIdOrElseThrowShouldCallDeleteByUserInRepoWithCorrectUser() {
        // given
        UUID id = UUID.randomUUID();
        UserEntity user = new UserEntity();
        // when
        when(userService.findUserEntityByIdOrElseThrow(id)).thenReturn(user);
        underTest.deleteRefreshTokenByUserIdOrElseThrow(id);
        verify(refreshTokenRepository).deleteByUser(user);
    }
}
