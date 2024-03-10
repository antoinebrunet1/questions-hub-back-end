package com.example.questionshub.question.service;

import com.example.questionshub.exceptions.NotFoundException;
import com.example.questionshub.question.entity.QuestionEntity;
import com.example.questionshub.question.repository.QuestionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class QuestionService {
    private final QuestionRepository repo;

    public Iterable<QuestionEntity> findAllQuestions() {
        return repo.findAll();
    }

    public void removeQuestionById(UUID id) {
        repo.deleteById(id);
    }

    public QuestionEntity addQuestion(QuestionEntity question) {
        return repo.save(question);
    }

    public void updateQuestion(UUID id, QuestionEntity question) {
        findByIdOrElseThrow(id);
        repo.save(question);
    }

    public QuestionEntity findByIdOrElseThrow(final UUID id) {
        return repo
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException("Question by id " + id + " was not found")
                );
    }
}
