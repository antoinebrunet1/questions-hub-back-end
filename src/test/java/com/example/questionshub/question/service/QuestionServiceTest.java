package com.example.questionshub.question.service;

import com.example.questionshub.question.entity.QuestionEntity;
import com.example.questionshub.question.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionService underTest;

    @Test
    void findAllQuestionsShouldCallFindAllInRepo() {
        // when
        underTest.findAllQuestions();
        // then
        verify(questionRepository).findAll();
    }

    @Test
    void removeQuestionByIdShouldCallDeleteByIdInRepoWithCorrectId() {
        // given
        UUID id = UUID.randomUUID();
        // when
        underTest.removeQuestionById(id);
        //then
        verify(questionRepository).deleteById(id);
    }

    @Test
    void addQuestionShouldCallSaveInRepoWithCorrectQuestion() {
        // given
        QuestionEntity question = new QuestionEntity(
                null,
                "Sample body",
                "Sample answer"
        );
        // when
        underTest.addQuestion(question);
        //then
        verify(questionRepository).save(question);
    }

    @Test
    void updateQuestionShouldCallSaveInRepoWithCorrectQuestion() {
        // given
        UUID id = UUID.randomUUID();
        QuestionEntity question = new QuestionEntity(
                null,
                "Sample body",
                "Sample answer"
        );
        Optional<QuestionEntity> questionEntityOptional = Optional.of(question);
        when(questionRepository.findById(id)).thenReturn(questionEntityOptional);
        // when
        underTest.updateQuestion(id, question);
        //then
        verify(questionRepository).save(question);
    }

    @Test
    void findByIdOrElseThrowShouldCallFindByIdInRepoWithCorrectId() {
        // given
        UUID id = UUID.randomUUID();
        QuestionEntity question = new QuestionEntity();
        Optional<QuestionEntity> questionEntityOptional = Optional.of(question);
        // when
        when(questionRepository.findById(id)).thenReturn(questionEntityOptional);
        underTest.findByIdOrElseThrow(id);
        //then
        verify(questionRepository).findById(id);
    }
}
