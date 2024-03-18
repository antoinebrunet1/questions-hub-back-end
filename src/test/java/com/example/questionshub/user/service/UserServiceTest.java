package com.example.questionshub.user.service;

import com.example.questionshub.user.dto.UserDto;
import com.example.questionshub.user.entity.UserEntity;
import com.example.questionshub.user.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private UserService underTest;

    @Test
    void searchByEmailShouldCallFindByEmailInRepoWithCorrectEmail() {
        // given
        String email = "test@test.com";
        // when
        underTest.searchByEmail(email);
        // then
        verify(userRepository).findByEmail(email);
    }

    @Test
    void findAllUsersShouldCallFindAllInRepo() {
        // when
        underTest.findAllUsers();
        // then
        verify(userRepository).findAll();
    }

    @Test
    void findUserDtoByIdOrElseThrowShouldCallFindByIdInRepoWithCorrectId() {
        // given
        UUID id = UUID.randomUUID();
        Optional<UserEntity> userEntityOptional = Optional.of(new UserEntity());
        // when
        when(userRepository.findById(id)).thenReturn(userEntityOptional);
        underTest.findUserEntityByIdOrElseThrow(id);
        // then
        verify(userRepository).findById(id);
    }

    @Test
    void createUserShouldCallSelectExistsEmailInRepoWithCorrectEmail()
            throws BadRequestException, NoSuchAlgorithmException {
        // given
        String email = "test@test.com";
        String password = "password";
        UserDto user = new UserDto(
                null,
                email,
                password
        );
        // when
        UserEntity userEntity = new UserEntity(email);
        when(mapper.map(user, UserEntity.class)).thenReturn(userEntity);
        underTest.createUser(user, password);
        // then
        verify(userRepository).selectExistsEmail(email);
    }

    @Test
    void createUserShouldCallSaveInRepo() throws BadRequestException, NoSuchAlgorithmException {
        // given
        String email = "test@test2.com";
        String password = "password";
        UserDto userDto = new UserDto(
                UUID.randomUUID(),
                email,
                password
        );
        UserEntity userEntity = new UserEntity(email);
        // when
        when(mapper.map(userDto, UserEntity.class)).thenReturn(userEntity);
        underTest.createUser(userDto, password);
        // then
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void updateUserShouldCallSaveInRepoWithCorrectUserEntity() throws NoSuchAlgorithmException {
        // given
        String email = "test@test.com";
        UUID id = UUID.randomUUID();
        String password = "password";
        UserDto userDto = new UserDto(
                null,
                email,
                password
        );
        UserEntity userEntity = new UserEntity();
        Optional<UserEntity> userEntityOptional = Optional.of(userEntity);
        // when
        when(userRepository.findById(id)).thenReturn(userEntityOptional);
        when(mapper.map(userDto, UserEntity.class)).thenReturn(userEntity);
        underTest.updateUser(id, userDto, password);
        userEntity.setEmail(email);
        // then
        verify(userRepository).save(userEntity);
    }

    @Test
    void removeUserByIdShouldCallDeleteByIdInRepoWithCorrectId() {
        // given
        UUID id = UUID.randomUUID();
        Optional<UserEntity> userEntityOptional = Optional.of(new UserEntity());
        // when
        when(userRepository.findById(id)).thenReturn(userEntityOptional);
        underTest.removeUserById(id);
        // then
        verify(userRepository).deleteById(id);
    }

    @Test
    void findUserEntityByIdOrElseThrowShouldCallFindByIdInRepoWithCorrectId() {
        // given
        UUID id = UUID.randomUUID();
        Optional<UserEntity> userEntityOptional = Optional.of(new UserEntity());
        // when
        when(userRepository.findById(id)).thenReturn(userEntityOptional);
        underTest.findUserEntityByIdOrElseThrow(id);
        // then
        verify(userRepository).findById(id);
    }
}
