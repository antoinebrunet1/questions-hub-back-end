package com.example.questionshub.user.entity;

import com.example.questionshub.question.entity.QuestionEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")

    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(unique = true)
    private String email;

    private byte[] storedHash;

    private byte[] storedSalt;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Set<QuestionEntity> questions = new HashSet<>();

    public UserEntity(String email) {
        this.email = email;
    }

    public void addQuestion(QuestionEntity question) {
        questions.add(question);
    }
}