package com.example.questionshub.user.repository;

import com.example.questionshub.user.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository underTest;

    @Test
    void selectExistsEmailShouldReturnTrueWhenEmailExists() {
        // given
        String email = "test@test.com";
        UserEntity user = new UserEntity(email);
        underTest.save(user);
        // when
        boolean returnValueOfSelectExistsEmail = underTest.selectExistsEmail(email);
        // then
        assertThat(returnValueOfSelectExistsEmail).isTrue();
    }

    @Test
    void selectExistsEmailShouldReturnFalseWhenEmailDoesNotExist() {
        // given
        String email = "test@test.com";
        // when
        boolean returnValueOfSelectExistsEmail = underTest.selectExistsEmail(email);
        // then
        assertThat(returnValueOfSelectExistsEmail).isFalse();
    }

    @Test
    void findByEmailShouldReturnCorrectUserWithEmailOfExistingUser() {
        // given
        String email = "test@test.com";
        UserEntity user = new UserEntity(email);
        UserEntity savedUser = underTest.save(user);
        // when
        UserEntity capturedUser = underTest.findByEmail(email);
        // then
        assertThat(capturedUser).isEqualTo(savedUser);
    }
}
