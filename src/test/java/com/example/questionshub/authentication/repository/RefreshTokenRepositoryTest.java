package com.example.questionshub.authentication.repository;

import com.example.questionshub.authentication.models.RefreshTokenEntity;
import com.example.questionshub.user.entity.UserEntity;
import com.example.questionshub.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class RefreshTokenRepositoryTest {
    @Autowired
    private RefreshTokenRepository underTest;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByTokenShouldFindCorrectRefreshTokenEntity() {
        // given
        UserEntity user = new UserEntity();
        UserEntity savedUser = userRepository.save(user);
        String refreshToken = UUID.randomUUID().toString();
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setUser(savedUser);
        refreshTokenEntity.setExpiryDate(Instant.now());
        refreshTokenEntity.setToken(refreshToken);
        RefreshTokenEntity savedRefreshTokenEntity = underTest.save(refreshTokenEntity);
        // when
        Optional<RefreshTokenEntity> refreshTokenEntityOptional = underTest.findByToken(refreshToken);
        // then
        assertThat(
                refreshTokenEntityOptional.isPresent()
                        && savedRefreshTokenEntity.equals(refreshTokenEntityOptional.get()))
                .isTrue();
    }

    @Test
    void deleteByUserShouldDeleteRefreshTokenEntity() {
        // given
        UserEntity user = new UserEntity();
        UserEntity savedUser = userRepository.save(user);
        String refreshToken = UUID.randomUUID().toString();
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setUser(savedUser);
        refreshTokenEntity.setExpiryDate(Instant.now());
        refreshTokenEntity.setToken(refreshToken);
        RefreshTokenEntity savedRefreshTokenEntity = underTest.save(refreshTokenEntity);
        // when
        underTest.deleteByUser(savedUser);
        // then
        Optional<RefreshTokenEntity> refreshTokenEntityOptional = underTest.findById(savedRefreshTokenEntity.getId());
        boolean refreshTokenEntityWithCorrectTokenExists = refreshTokenEntityOptional.isPresent();
        assertThat(refreshTokenEntityWithCorrectTokenExists).isFalse();
    }
}
