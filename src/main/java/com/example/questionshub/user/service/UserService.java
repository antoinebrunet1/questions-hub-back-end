package com.example.questionshub.user.service;

import com.example.questionshub.exceptions.NotFoundException;
import com.example.questionshub.user.dto.UserDto;
import com.example.questionshub.user.entity.UserEntity;
import com.example.questionshub.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final ModelMapper mapper;
    private final UserRepository repo;

    private UserDto convertToDto(UserEntity entity) {
        return mapper.map(entity, UserDto.class);
    }

    private UserEntity convertToEntity(UserDto dto) {
        return mapper.map(dto, UserEntity.class);
    }

    public UserEntity searchByEmail(String email) {
        return repo.findByEmail(email);
    }

    public List<UserDto> findAllUsers() {
        List<UserEntity> userEntityList = new ArrayList<>(repo.findAll());

        return userEntityList
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto findUserDtoByIdOrElseThrow(final UUID id) {
        UserEntity user = repo
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException("User by id " + id + " was not found")
                );

        return convertToDto(user);
    }

    private byte[] createSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[128];
        random.nextBytes(salt);

        return salt;
    }

    private byte[] createPasswordHash(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        messageDigest.update(salt);

        return messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
    }

    public UserDto createUser(UserDto userDto, String password) throws NoSuchAlgorithmException, BadRequestException {
        UserEntity user = convertToEntity(userDto);

        if (password.isBlank()) {
            throw new IllegalArgumentException("Password is required.");
        }

        Boolean existsEmail = repo.selectExistsEmail(user.getEmail());

        if (existsEmail) {
            throw new BadRequestException("Email " + user.getEmail() + " taken");
        }

        byte[] salt = createSalt();
        byte[] hashedPassword = createPasswordHash(password, salt);
        user.setStoredSalt(salt);
        user.setStoredHash(hashedPassword);
        repo.save(user);

        return convertToDto(user);
    }

    public void updateUser(UUID id, UserDto userDto, String password)
            throws NoSuchAlgorithmException {
        UserEntity user = findUserEntityByIdOrElseThrow(id);
        UserEntity userParam = convertToEntity(userDto);
        user.setEmail(userParam.getEmail());

        if (!password.isBlank()) {
            byte[] salt = createSalt();
            byte[] hashedPassword =
                    createPasswordHash(password, salt);
            user.setStoredSalt(salt);
            user.setStoredHash(hashedPassword);
        }

        repo.save(user);
    }

    public void removeUserById(UUID id) {
        findUserEntityByIdOrElseThrow(id);
        repo.deleteById(id);
    }

    public UserEntity findUserEntityByIdOrElseThrow(final UUID id) {
        return repo
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException("User by id " + id + " was not found")
                );
    }
}
