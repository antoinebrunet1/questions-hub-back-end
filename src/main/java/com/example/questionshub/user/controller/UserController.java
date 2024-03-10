package com.example.questionshub.user.controller;

import com.example.questionshub.user.dto.UserDto;
import com.example.questionshub.user.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Iterable<UserDto> getUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") UUID id) {
        return userService.findUserDtoByIdOrElseThrow(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable("id") UUID id) {
        userService.removeUserById(id);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto postUser(@Valid @RequestBody UserDto userDto) throws NoSuchAlgorithmException, BadRequestException {
        return userService.createUser(userDto, userDto.getPassword());
    }

    @PutMapping("/{id}")
    public void putUser(@PathVariable("id") UUID id, @Valid @RequestBody UserDto userDto)
            throws NoSuchAlgorithmException {
        userService.updateUser(id, userDto, userDto.getPassword());
    }
}
