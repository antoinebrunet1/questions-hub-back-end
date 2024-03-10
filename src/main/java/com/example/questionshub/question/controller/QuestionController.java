package com.example.questionshub.question.controller;

import com.example.questionshub.authentication.util.JwtUtil;
import com.example.questionshub.question.dto.QuestionDto;
import com.example.questionshub.question.entity.QuestionEntity;
import com.example.questionshub.question.service.QuestionService;
import com.example.questionshub.user.entity.UserEntity;
import com.example.questionshub.user.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("api/v1/questions")
@PreAuthorize("isAuthenticated()")
@AllArgsConstructor
public class QuestionController {
    private final QuestionService questionService;
    private final UserService userService;
    private final ModelMapper mapper;
    private final JwtUtil jwtUtil;

    private QuestionDto convertToDto(QuestionEntity entity) {
        return mapper.map(entity, QuestionDto.class);
    }
    private QuestionEntity convertToEntity(QuestionDto dto) {
        return mapper.map(dto, QuestionEntity.class);
    }

    @GetMapping("/{id}")
    public QuestionDto getQuestionById(@PathVariable("id") UUID id) {
        return convertToDto(questionService.findByIdOrElseThrow(id));
    }

    @PostMapping
    public QuestionDto postQuestion(@RequestHeader (HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                    @Valid @RequestBody QuestionDto questionDto) {
        String jwt = authorizationHeader.substring(7);
        String username = jwtUtil.extractUsername(jwt);
        UserEntity user = userService.searchByEmail(username);
        QuestionEntity entity = convertToEntity(questionDto);
        user.addQuestion(entity);
        QuestionEntity question = questionService.addQuestion(entity);

        return convertToDto(question);
    }

    @PutMapping("/{id}")
    public void putQuestion(@PathVariable("id") UUID id, @Valid @RequestBody QuestionDto questionDto) {
        if (!id.equals(questionDto.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id does not match");
        }

        QuestionEntity questionEntity = convertToEntity(questionDto);
        questionService.updateQuestion(id, questionEntity);
    }

    @DeleteMapping("/{id}")
    public void deleteQuestionById(@PathVariable("id") UUID id) {
        questionService.removeQuestionById(id);
    }

    @GetMapping
    public List<QuestionDto> getQuestions() {
        List<QuestionEntity> questionsList = StreamSupport
                .stream(
                        questionService
                                .findAllQuestions()
                                .spliterator(),
                        false)
                .toList();

        return questionsList
                .stream()
                .map(this::convertToDto)
                .toList();
    }
}
