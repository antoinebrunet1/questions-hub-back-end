package com.example.questionshub.question.repository;

import com.example.questionshub.question.entity.QuestionEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface QuestionRepository extends CrudRepository<QuestionEntity, UUID> {
}
